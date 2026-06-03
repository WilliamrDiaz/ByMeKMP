package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
)