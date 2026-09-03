package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["conversationId"])]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val role: String, // "user", "model", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isImage: Boolean = false,
    val imageUriOrPath: String? = null,
    val imagePrompt: String? = null,
    val aspectRatio: String? = "1:1",
    val attachmentsJson: String? = null,
    val status: String = "SUCCESS", // "SUCCESS", "LOADING", "ERROR"
    val errorMessage: String? = null
)
