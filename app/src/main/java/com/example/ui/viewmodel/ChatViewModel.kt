package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.ConversationEntity
import com.example.data.local.MessageEntity
import com.example.data.model.Attachment
import com.example.data.model.ImageOptions
import com.example.data.model.UserAccount
import com.example.data.repository.ChatRepository
import com.example.data.repository.NetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("evoro_prefs", Context.MODE_PRIVATE)

    private val database = AppDatabase.getDatabase(application)
    private val geminiClient = GeminiClient(application)
    private val networkMonitor = NetworkMonitor(application)
    private val repository = ChatRepository(application, database, geminiClient, networkMonitor)

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val conversations: StateFlow<List<ConversationEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllConversations()
        } else {
            repository.searchConversations(query.trim())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<MessageEntity>> = _activeConversationId.flatMapLatest { convId ->
        if (convId == null) {
            flowOf(emptyList())
        } else {
            repository.getMessages(convId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _attachments = MutableStateFlow<List<Attachment>>(emptyList())
    val attachments: StateFlow<List<Attachment>> = _attachments.asStateFlow()

    private val _imageOptions = MutableStateFlow(ImageOptions())
    val imageOptions: StateFlow<ImageOptions> = _imageOptions.asStateFlow()

    private val _selectedModel = MutableStateFlow(
        prefs.getString("pref_model", "gemini-3.5-flash") ?: "gemini-3.5-flash"
    )
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _userAccount = MutableStateFlow(
        UserAccount(
            id = prefs.getString("user_id", "guest") ?: "guest",
            email = prefs.getString("user_email", null),
            name = prefs.getString("user_name", "Guest User") ?: "Guest User",
            isGuest = prefs.getBoolean("user_is_guest", true)
        )
    )
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
    }

    fun setOnboardingCompleted() {
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun addAttachment(attachment: Attachment) {
        _attachments.value = _attachments.value + attachment
    }

    fun removeAttachment(attachment: Attachment) {
        _attachments.value = _attachments.value.filter { it.id != attachment.id }
    }

    fun clearAttachments() {
        _attachments.value = emptyList()
    }

    fun setImageOptions(options: ImageOptions) {
        _imageOptions.value = options
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        prefs.edit().putString("pref_model", model).apply()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
    }

    fun startNewChat() {
        _activeConversationId.value = null
        _inputText.value = ""
        _attachments.value = emptyList()
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        val currentAttachments = _attachments.value
        if (text.isEmpty() && currentAttachments.isEmpty()) return

        val currentModel = _selectedModel.value
        val currentOptions = _imageOptions.value

        // Clear composer input immediately for snappy responsive feel
        _inputText.value = ""
        _attachments.value = emptyList()
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                var convId = _activeConversationId.value
                if (convId == null) {
                    convId = repository.createConversation(
                        title = "New Chat",
                        modelName = currentModel
                    )
                    _activeConversationId.value = convId
                }

                repository.sendMessage(
                    conversationId = convId,
                    userPrompt = text,
                    attachments = currentAttachments,
                    imageOptions = currentOptions,
                    modelName = currentModel
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun regenerateMessage(message: MessageEntity) {
        val convId = message.conversationId
        val prompt = message.imagePrompt ?: message.content
        if (prompt.isBlank()) return

        _isGenerating.value = true
        viewModelScope.launch {
            try {
                // Delete previous error/outdated response
                repository.deleteMessage(message.id)
                repository.sendMessage(
                    conversationId = convId,
                    userPrompt = prompt,
                    attachments = emptyList(),
                    imageOptions = _imageOptions.value,
                    modelName = _selectedModel.value
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun deleteMessage(id: String) {
        viewModelScope.launch {
            repository.deleteMessage(id)
        }
    }

    fun pinConversation(id: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.setPinned(id, isPinned)
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            repository.renameConversation(id, newTitle)
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = null
            }
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            repository.clearAllConversations()
            _activeConversationId.value = null
        }
    }

    fun loginUser(email: String, name: String) {
        prefs.edit()
            .putString("user_id", java.util.UUID.randomUUID().toString())
            .putString("user_email", email)
            .putString("user_name", name)
            .putBoolean("user_is_guest", false)
            .apply()

        _userAccount.value = UserAccount(
            id = "user_account",
            email = email,
            name = name,
            isGuest = false,
            isConfigured = true
        )
    }

    fun logoutUser() {
        prefs.edit()
            .remove("user_id")
            .remove("user_email")
            .remove("user_name")
            .putBoolean("user_is_guest", true)
            .apply()

        _userAccount.value = UserAccount()
    }

    fun deleteAccount() {
        logoutUser()
        clearAllConversations()
    }
}
