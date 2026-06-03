package com.byme.app.domain.repository

import com.byme.app.domain.model.User

interface UserRepositoryInterface {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun getProfessionals(): Result<List<User>>
    suspend fun searchProfessionals(query: String): Result<List<User>>
}