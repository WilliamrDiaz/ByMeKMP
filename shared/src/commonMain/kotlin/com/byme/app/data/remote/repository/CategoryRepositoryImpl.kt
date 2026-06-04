package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Category
import com.byme.app.domain.repository.CategoryRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class CategoryRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CategoryRepositoryInterface {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val snapshot = firestore
                .collection("categories")
                .get()

            val categories = snapshot.documents.map { it.data<Category>() }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}