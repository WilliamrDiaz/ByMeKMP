package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.ServiceRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class ServiceRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ServiceRepositoryInterface {

    override suspend fun getServices(userId: String): Result<List<Service>> {
        return try {
            val snapshot = firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .get()

            val services = snapshot.documents.map {
                it.data<Service>().copy(id = it.id)
            }

            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addService(userId: String, service: Service): Result<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .add(service)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteService(userId: String, serviceId: String): Result<Unit> {
        return try {
            firestore
                .collection("users")
                .document(userId)
                .collection("services")
                .document(serviceId)
                .delete()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}