package com.byme.app.domain.usecase

import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(
    private val chatRepository: ChatRepositoryInterface,
) {
    suspend operator fun invoke(chatId: String): Flow<List<Message>> {
        return chatRepository.getMessages(chatId)
    }
}