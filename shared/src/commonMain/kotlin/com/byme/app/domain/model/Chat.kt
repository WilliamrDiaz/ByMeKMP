package com.byme.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: String = "",
    val userId: String = "",
    val professionalId: String = "",
    val userName: String = "",
    val professionalName: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0,
)