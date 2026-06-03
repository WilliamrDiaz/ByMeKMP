package com.byme.app.domain.usecase

import dev.gitlive.firebase.auth.FirebaseAuth

class LoginUseCase (
    private val auth: FirebaseAuth,
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}