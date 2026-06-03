package com.byme.app.domain.usecase

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface
import dev.gitlive.firebase.auth.FirebaseAuth

class RegisterUseCase(
    private val userRepository: UserRepositoryInterface,
    private val auth: FirebaseAuth,
) {

    suspend operator fun invoke(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return try {
            // Crear en Firebase Auth (Multiplataforma)
            val authResult = auth.createUserWithEmailAndPassword(email, password)

            val userId = authResult.user?.uid
                ?: return Result.failure(Exception("Error al obtener el ID del usuario"))

            // Guardar en Firestore usando el repositorio compartido
            val user = User(
                id = userId,
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                isProfessional = false,
                role = "user"
            )

            userRepository.createUser(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}