package com.example.data.model

data class UserAccount(
    val id: String = "guest_user",
    val email: String? = null,
    val name: String = "Guest User",
    val isGuest: Boolean = true,
    val isConfigured: Boolean = false
)
