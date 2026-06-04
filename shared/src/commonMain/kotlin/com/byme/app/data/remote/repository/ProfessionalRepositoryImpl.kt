package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Professional
import dev.gitlive.firebase.firestore.FirebaseFirestore

class ProfessionalRepositoryImpl (
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("professional")

    suspend fun getAllProfessionals(): List<Professional> {
        return try {
            // En KMP no necesitas .await(), get() ya es suspend
            val snapshot = collection.get()
            snapshot.documents.map { doc ->
                doc.data<Professional>().copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchProfessionals(query: String): List<Professional> {
        return try {
            val snapshot = collection.get()
            snapshot.documents.map { doc ->
                doc.data<Professional>().copy(id = doc.id)
            }.filter {
                // Lógica de filtrado
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}