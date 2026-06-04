package com.byme.app.data.remote.repository

import com.byme.app.data.local.UserLocalDataSource
import com.byme.app.domain.model.User
import com.byme.app.domain.repository.UserRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val localDataSource: UserLocalDataSource
) : UserRepositoryInterface {

    private val usersCollection = firestore.collection("users")

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            // En KMP usamos .set(user) directamente gracias a @Serializable
            usersCollection.document(user.id).set(user) // guardamos en Firestore
            localDataSource.insertUser(user)// guardamos en local
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        println("Buscando usuario en Firestore con ID: $userId")
        return try {
            // Primero intentar buscar en la base de datos local (iPhone/Android)
            val cachedUser = localDataSource.getUserById(userId)
            if (cachedUser != null) {
                return Result.success(cachedUser)
            }

            // Si no está en local, buscar en Firestore
            val document = usersCollection.document(userId).get()
            if (document.exists) {
                val user = document.data<User>().copy(id = userId)

                // Guardar en local para la próxima vez
                localDataSource.insertUser(user)

                Result.success(user)
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
            usersCollection.document(user.id).set(user, merge = true) // Actualizamos en Firestore

            localDataSource.insertUser(user) // Actualizamos en local

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

            professionals.forEach { localDataSource.insertUser(it) }

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