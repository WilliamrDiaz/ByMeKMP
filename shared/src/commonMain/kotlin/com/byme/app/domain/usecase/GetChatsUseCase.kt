package com.byme.app.domain.usecase

import com.byme.app.domain.model.Chat
import com.byme.app.domain.repository.ChatRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetChatsUseCase(
    private val chatRepository: ChatRepositoryInterface
) {
    // devuelve un Flow para que la UI se actualice sola
    suspend operator fun invoke(userId: String): Flow<List<Chat>> {
        return chatRepository.getChats(userId)
    }
}