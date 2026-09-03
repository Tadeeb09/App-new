package com.example.data.repository

import android.content.Context
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import com.example.data.model.Attachment
import com.example.data.model.ImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.util.UUID

class ChatRepository(
    private val context: Context,
    private val database: AppDatabase,
    val geminiClient: GeminiClient,
    private val networkMonitor: NetworkMonitor
) {
    private val conversationDao = database.conversationDao()
    private val messageDao = database.messageDao()

    fun getAllConversations(): Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()

    fun searchConversations(query: String): Flow<List<ConversationEntity>> =
        conversationDao.searchConversations(query)

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesForConversation(conversationId)

    suspend fun createConversation(
        title: String = "New Chat",
        modelName: String = "gemini-3.5-flash"
    ): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val conversation = ConversationEntity(
            id = id,
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isPinned = false,
            modelName = modelName
        )
        conversationDao.insert(conversation)
        id
    }

    suspend fun sendMessage(
        conversationId: String,
        userPrompt: String,
        attachments: List<Attachment> = emptyList(),
        imageOptions: ImageOptions = ImageOptions(),
        modelName: String = "gemini-3.5-flash"
    ) = withContext(Dispatchers.IO) {
        val trimmedPrompt = userPrompt.trim()
        if (trimmedPrompt.isEmpty() && attachments.isEmpty()) return@withContext

        val isOnline = networkMonitor.checkCurrentConnectivity()

        // 1. Save User Message
        val userMsgId = UUID.randomUUID().toString()
        val attachmentsJson = if (attachments.isNotEmpty()) {
            val arr = JSONArray()
            for (att in attachments) {
                arr.put(JSONObject().apply {
                    put("id", att.id)
                    put("name", att.fileName)
                    put("mime", att.mimeType)
                    put("size", att.sizeBytes)
                    put("uri", att.uriString)
                    put("isImage", att.isImage)
                })
            }
            arr.toString()
        } else null

        val userMessage = MessageEntity(
            id = userMsgId,
            conversationId = conversationId,
            role = "user",
            content = trimmedPrompt,
            timestamp = System.currentTimeMillis(),
            attachmentsJson = attachmentsJson,
            status = "SUCCESS"
        )
        messageDao.insert(userMessage)
        conversationDao.updateTimestamp(conversationId)

        // Check if conversation title should be updated
        val currentConv = conversationDao.getConversationById(conversationId)
        if (currentConv != null && (currentConv.title == "New Chat" || currentConv.title.isBlank())) {
            val newTitle = if (trimmedPrompt.isNotEmpty()) {
                val words = trimmedPrompt.split("\\s+".toRegex()).take(5).joinToString(" ")
                if (words.length > 30) words.take(30) + "…" else words
            } else {
                attachments.firstOrNull()?.fileName ?: "Chat"
            }
            conversationDao.updateTitle(conversationId, newTitle)
        }

        // 2. Insert AI Loading Message
        val aiMsgId = UUID.randomUUID().toString()
        val isExplicitImageIntent = geminiClient.isImageGenerationIntent(trimmedPrompt)
        val isEditIntent = geminiClient.isImageEditIntent(trimmedPrompt)
        val hasAttachedImage = attachments.any { it.isImage && !it.base64Data.isNullOrEmpty() }

        val placeholderMessage = MessageEntity(
            id = aiMsgId,
            conversationId = conversationId,
            role = "model",
            content = if (isExplicitImageIntent || (isEditIntent && hasAttachedImage)) "Creating your image…" else "",
            timestamp = System.currentTimeMillis() + 1,
            isImage = isExplicitImageIntent || (isEditIntent && hasAttachedImage),
            status = "LOADING"
        )
        messageDao.insert(placeholderMessage)

        if (!isOnline) {
            messageDao.updateContent(
                id = aiMsgId,
                content = "",
                status = "ERROR",
                errorMessage = "You're offline. Check your connection and try again."
            )
            return@withContext
        }

        // 3. Process either Image Generation / Editing or Text Chat
        if (isExplicitImageIntent || (isEditIntent && (hasAttachedImage || getLatestImage(conversationId) != null))) {
            handleImageRequest(
                aiMsgId = aiMsgId,
                prompt = trimmedPrompt,
                attachments = attachments,
                imageOptions = imageOptions,
                conversationId = conversationId,
                isEdit = isEditIntent
            )
        } else {
            handleTextChatRequest(
                aiMsgId = aiMsgId,
                conversationId = conversationId,
                prompt = trimmedPrompt,
                attachments = attachments,
                modelName = modelName
            )
        }
    }

    private suspend fun handleImageRequest(
        aiMsgId: String,
        prompt: String,
        attachments: List<Attachment>,
        imageOptions: ImageOptions,
        conversationId: String,
        isEdit: Boolean
    ) {
        var baseImageBytes: ByteArray? = null

        // If user attached an image, use it for editing/reference
        val attachedImage = attachments.firstOrNull { it.isImage && !it.base64Data.isNullOrEmpty() }
        if (attachedImage != null && attachedImage.base64Data != null) {
            baseImageBytes = android.util.Base64.decode(attachedImage.base64Data, android.util.Base64.DEFAULT)
        } else if (isEdit) {
            // Retrieve previous image from chat history
            val prevImgMsg = messageDao.getLatestImageForConversation(conversationId)
            if (prevImgMsg?.imageUriOrPath != null) {
                val prevFile = File(prevImgMsg.imageUriOrPath)
                if (prevFile.exists()) {
                    baseImageBytes = withContext(Dispatchers.IO) {
                        FileInputStream(prevFile).use { it.readBytes() }
                    }
                }
            }
        }

        val result = geminiClient.generateOrEditImage(
            prompt = prompt,
            options = imageOptions,
            baseImageBytes = baseImageBytes,
            isEditing = baseImageBytes != null
        )

        result.fold(
            onSuccess = { imageResult ->
                val updatedMessage = MessageEntity(
                    id = aiMsgId,
                    conversationId = conversationId,
                    role = "model",
                    content = imageResult.description,
                    timestamp = System.currentTimeMillis(),
                    isImage = true,
                    imageUriOrPath = imageResult.filePath,
                    imagePrompt = prompt,
                    aspectRatio = imageResult.aspectRatio,
                    status = "SUCCESS"
                )
                messageDao.update(updatedMessage)
            },
            onFailure = {
                val updatedMessage = MessageEntity(
                    id = aiMsgId,
                    conversationId = conversationId,
                    role = "model",
                    content = "",
                    timestamp = System.currentTimeMillis(),
                    isImage = true,
                    status = "ERROR",
                    errorMessage = "I couldn't create that image. Please try again."
                )
                messageDao.update(updatedMessage)
            }
        )
    }

    private suspend fun handleTextChatRequest(
        aiMsgId: String,
        conversationId: String,
        prompt: String,
        attachments: List<Attachment>,
        modelName: String
    ) {
        // Build conversation history turns
        val pastMessages = messageDao.getMessagesForConversation(conversationId).firstOrNull() ?: emptyList()
        val historyTurns = pastMessages
            .filter { it.id != aiMsgId && it.status == "SUCCESS" && !it.isImage }
            .map { it.role to it.content }

        var accumulated = ""
        val result = geminiClient.generateChatResponse(
            prompt = prompt,
            history = historyTurns,
            attachments = attachments,
            modelName = modelName,
            onChunk = { chunk ->
                accumulated = chunk
                messageDao.updateContent(aiMsgId, accumulated, "LOADING")
            }
        )

        result.fold(
            onSuccess = { finalText ->
                messageDao.updateContent(aiMsgId, finalText, "SUCCESS")
            },
            onFailure = {
                messageDao.updateContent(
                    id = aiMsgId,
                    content = accumulated,
                    status = "ERROR",
                    errorMessage = "Something went wrong. Please try again."
                )
            }
        )
    }

    suspend fun getLatestImage(conversationId: String): MessageEntity? {
        return messageDao.getLatestImageForConversation(conversationId)
    }

    suspend fun renameConversation(id: String, newTitle: String) = withContext(Dispatchers.IO) {
        conversationDao.updateTitle(id, newTitle)
    }

    suspend fun setPinned(id: String, isPinned: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.setPinned(id, isPinned)
    }

    suspend fun deleteConversation(id: String) = withContext(Dispatchers.IO) {
        messageDao.deleteByConversationId(id)
        conversationDao.deleteById(id)
    }

    suspend fun clearAllConversations() = withContext(Dispatchers.IO) {
        messageDao.clearAll()
        conversationDao.clearAll()
    }

    suspend fun deleteMessage(id: String) = withContext(Dispatchers.IO) {
        messageDao.deleteById(id)
    }
}
