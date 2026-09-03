package com.example.data.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.Attachment
import com.example.data.model.ImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiClient(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (_: Exception) {
            ""
        }
    }

    fun isConfigured(): Boolean {
        return getApiKey().isNotEmpty()
    }

    /**
     * Detects if the prompt has an image-generation intent.
     */
    fun isImageGenerationIntent(prompt: String): Boolean {
        val lower = prompt.trim().lowercase()
        val keywords = listOf(
            "create an image", "create a picture", "generate an image", "generate a picture",
            "make an image", "draw an image", "draw a picture", "create a logo",
            "create a gaming logo", "create a professional logo", "create a youtube thumbnail",
            "create a youtube banner", "generate a futuristic", "generate art", "generate photo",
            "make a logo", "design a logo", "create art", "draw me", "generate image",
            "create image", "render an image", "photo of", "illustration of"
        )
        if (keywords.any { lower.contains(it) }) return true
        if (lower.startsWith("generate ") && (lower.contains("logo") || lower.contains("wallpaper") || lower.contains("scene") || lower.contains("city") || lower.contains("character"))) {
            return true
        }
        if (lower.startsWith("create ") && (lower.contains("logo") || lower.contains("banner") || lower.contains("thumbnail") || lower.contains("avatar") || lower.contains("poster"))) {
            return true
        }
        return false
    }

    /**
     * Detects if the prompt has an image-editing intent.
     */
    fun isImageEditIntent(prompt: String): Boolean {
        val lower = prompt.trim().lowercase()
        val editKeywords = listOf(
            "remove background", "change background", "make it hd", "make it cinematic",
            "remove object", "remove the object", "remove person", "remove the person",
            "change colors", "change color", "turn it into", "make it red", "make it blue",
            "make it darker", "make it lighter", "edit this image", "edit image",
            "crop", "enhance", "recolor", "add background", "replace background"
        )
        return editKeywords.any { lower.contains(it) }
    }

    /**
     * Text & multimodal chat generation.
     */
    suspend fun generateChatResponse(
        prompt: String,
        history: List<Pair<String, String>>, // role to content
        attachments: List<Attachment> = emptyList(),
        modelName: String = "gemini-3.5-flash",
        onChunk: suspend (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException("AI Studio API key is not configured. Please configure your GEMINI_API_KEY in the Secrets panel.")
            )
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:streamGenerateContent?key=$apiKey&alt=sse"

            val contentsArray = JSONArray()

            // System instructions context (monochrome, direct, professional)
            val systemInstructionObj = JSONObject().apply {
                put("role", "system")
                val parts = JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are EVORO AI, an elite, minimalist, and precise AI assistant. Provide concise, clear, well-formatted markdown responses with clean syntax highlighting and bullet points where applicable. Avoid fluff, unnecessary disclaimers, or excessive verbosity.")
                    })
                }
                put("parts", parts)
            }

            // Append previous turns (up to 8 turns for latency and token safety)
            val recentHistory = history.takeLast(8)
            for ((role, text) in recentHistory) {
                val turn = JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    }
                    put("parts", parts)
                }
                contentsArray.put(turn)
            }

            // Current user turn
            val currentParts = JSONArray()
            currentParts.put(JSONObject().apply { put("text", prompt) })

            // Attachments (images or file text)
            for (att in attachments) {
                if (att.isImage && !att.base64Data.isNullOrEmpty()) {
                    currentParts.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", att.mimeType)
                            put("data", att.base64Data)
                        })
                    })
                } else if (!att.textContent.isNullOrEmpty()) {
                    currentParts.put(JSONObject().apply {
                        put("text", "Attached file [${att.fileName}]:\n${att.textContent}")
                    })
                }
            }

            val currentTurn = JSONObject().apply {
                put("role", "user")
                put("parts", currentParts)
            }
            contentsArray.put(currentTurn)

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstructionObj)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                })
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                return@withContext Result.failure(Exception("Generation failed with code ${response.code}: $errorBody"))
            }

            val fullText = StringBuilder()
            response.body?.byteStream()?.bufferedReader()?.use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line?.trim() ?: continue
                    if (currentLine.startsWith("data: ")) {
                        val dataJsonStr = currentLine.removePrefix("data: ").trim()
                        if (dataJsonStr.isNotEmpty() && dataJsonStr != "[DONE]") {
                            try {
                                val chunkObj = JSONObject(dataJsonStr)
                                val candidates = chunkObj.optJSONArray("candidates")
                                if (candidates != null && candidates.length() > 0) {
                                    val candidate = candidates.getJSONObject(0)
                                    val content = candidate.optJSONObject("content")
                                    val parts = content?.optJSONArray("parts")
                                    if (parts != null && parts.length() > 0) {
                                        for (i in 0 until parts.length()) {
                                            val part = parts.getJSONObject(i)
                                            val text = part.optString("text", "")
                                            if (text.isNotEmpty()) {
                                                fullText.append(text)
                                                onChunk(fullText.toString())
                                            }
                                        }
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }
            }

            val result = fullText.toString().trim()
            if (result.isEmpty()) {
                Result.success("I processed your request, but received an empty response. Please try again.")
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real Image Generation & Multi-turn Image Editing.
     * Uses gemini-3.1-flash-image-preview with fallback to gemini-2.5-flash-image.
     */
    suspend fun generateOrEditImage(
        prompt: String,
        options: ImageOptions = ImageOptions(),
        baseImageBytes: ByteArray? = null,
        isEditing: Boolean = false
    ): Result<ImageResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException("AI Studio API key is not configured. Please configure your GEMINI_API_KEY in the Secrets panel.")
            )
        }

        // We try gemini-3.1-flash-image-preview, falling back to gemini-2.5-flash-image
        val candidateModels = listOf("gemini-3.1-flash-image-preview", "gemini-2.5-flash-image")
        var lastException: Exception? = null

        for (model in candidateModels) {
            try {
                val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

                val partsArray = JSONArray()
                partsArray.put(JSONObject().apply {
                    put("text", prompt)
                })

                if (baseImageBytes != null && baseImageBytes.isNotEmpty()) {
                    val base64Image = Base64.encodeToString(baseImageBytes, Base64.NO_WRAP)
                    partsArray.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/png")
                            put("data", base64Image)
                        })
                    })
                }

                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", partsArray)
                    })
                }

                val imageConfig = JSONObject().apply {
                    put("aspectRatio", options.aspectRatio)
                    put("imageSize", if (options.quality == "High") "2K" else "1K")
                }

                val generationConfig = JSONObject().apply {
                    put("responseModalities", JSONArray().apply {
                        put("TEXT")
                        put("IMAGE")
                    })
                    put("imageConfig", imageConfig)
                }

                val requestJson = JSONObject().apply {
                    put("contents", contentsArray)
                    put("generationConfig", generationConfig)
                }

                val request = Request.Builder()
                    .url(endpoint)
                    .post(requestJson.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val err = response.body?.string() ?: ""
                    lastException = Exception("Model $model failed with code ${response.code}: $err")
                    continue
                }

                val bodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    lastException = Exception("No candidates returned from $model")
                    continue
                }

                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts == null || parts.length() == 0) {
                    lastException = Exception("No content parts in $model response")
                    continue
                }

                var foundImageBase64: String? = null
                var descriptionText: String? = null

                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    if (part.has("inlineData")) {
                        val inlineData = part.getJSONObject("inlineData")
                        foundImageBase64 = inlineData.optString("data")
                    } else if (part.has("text")) {
                        val t = part.optString("text")
                        if (t.isNotBlank()) {
                            descriptionText = t
                        }
                    }
                }

                if (!foundImageBase64.isNullOrEmpty()) {
                    val decodedBytes = Base64.decode(foundImageBase64, Base64.DEFAULT)
                    val savedFile = saveImageToInternalStorage(decodedBytes)
                    return@withContext Result.success(
                        ImageResult(
                            filePath = savedFile.absolutePath,
                            description = descriptionText ?: if (isEditing) "Image edited successfully" else "Image generated successfully",
                            aspectRatio = options.aspectRatio
                        )
                    )
                } else {
                    lastException = Exception("Model $model did not return an inline image part")
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        Result.failure(lastException ?: Exception("Image generation failed"))
    }

    private fun saveImageToInternalStorage(bytes: ByteArray): File {
        val imagesDir = File(context.filesDir, "evoro_images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val file = File(imagesDir, "evoro_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.png")
        FileOutputStream(file).use { fos ->
            fos.write(bytes)
            fos.flush()
        }
        return file
    }
}

data class ImageResult(
    val filePath: String,
    val description: String,
    val aspectRatio: String
)
