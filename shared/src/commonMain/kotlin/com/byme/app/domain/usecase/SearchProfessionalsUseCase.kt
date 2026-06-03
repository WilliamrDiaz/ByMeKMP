package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class SearchProfessionalsUseCase(
    private val userRepository: UserRepositoryInterface
) {
    suspend operator fun invoke(query: String): Result<List<User>> {
        return userRepository.searchProfessionals(query)
    }
}