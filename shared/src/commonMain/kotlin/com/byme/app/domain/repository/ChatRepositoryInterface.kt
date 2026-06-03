package com.byme.app.domain.repository

import com.byme.app.domain.model.Chat
import com.byme.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepositoryInterface {
    suspend fun getOrCreateChat(userId: String, professionalId: String, userName: String, professionalName: String): Result<Chat>
    suspend fun createChat(chat: Chat): Result<String>
    suspend fun getChats(userId: String): Flow<List<Chat>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    suspend fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun markAsRead(chatId: String, userId: String): Result<Unit>
}