package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class GetUserUseCase(
    private val userRepository: UserRepositoryInterface
) {
    // usamos el operador invoke para poder llamar al caso de uso como una función.
    suspend operator fun invoke(userId: String): Result<User> {
        return userRepository.getUser(userId)
    }
}