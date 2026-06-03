package com.byme.app.data

import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepositoryInterface {

    private val usersCollection = firestore.collection("users")

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            // En KMP usamos .set(user) directamente gracias a @Serializable
            usersCollection.document(user.id).set(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get()
            if (document.exists) {
                // .data<User>() mapea el JSON al objeto de forma automática
                Result.success(document.data())
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // merge = true para actualizar solo los campos modificados
            usersCollection.document(user.id).set(user, merge = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessionals(): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .where { "isProfessional" equalTo true }
                .get()

            // Mapeamos los documentos a objetos User
            val professionals = snapshot.documents.map { it.data<User>() }
            Result.success(professionals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProfessionals(query: String): Result<List<User>> {
        return try {
            val snapshot = usersCollection
                .where { "isProfessional" equalTo true }
                .get()

            val filtered = snapshot.documents
                .map { it.data<User>() }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true)
                }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}