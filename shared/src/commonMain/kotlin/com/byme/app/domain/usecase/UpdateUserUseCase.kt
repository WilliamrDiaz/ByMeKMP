package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface

class UpdateUserUseCase(
    private val userRepository: UserRepositoryInterface,
) {
    // recibe el objeto User completo y lo pasa al repositorio compartido.
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateUser(user)
    }
}