package com.byme.app.ui.state

import com.byme.app.domain.model.Chat
import com.byme.app.domain.model.Message

data class ChatListUiState(
    val isLoading: Boolean = false,
    val chats: List<Chat> = emptyList(),
    val pendingChats: List<Chat> = emptyList(),
    val drafts: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
)

data class ChatDetailUiState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val chatId: String = "",
    val professionalName: String = "",
    val errorMessage: String? = null,
)