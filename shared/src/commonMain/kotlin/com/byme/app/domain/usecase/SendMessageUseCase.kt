package com.byme.app.domain.usecase

import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface

class SendMessageUseCase(
    private val chatRepository: ChatRepositoryInterface
) {
    suspend operator fun invoke(
        chatId: String,
        message: Message
    ): Result<Unit> {
        return chatRepository.sendMessage(chatId, message)
    }
}