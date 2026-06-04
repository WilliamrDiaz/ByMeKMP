package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Review
import com.byme.app.domain.repository.ReviewRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class ReviewRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ReviewRepositoryInterface {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            reviewsCollection.add(review)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviews(professionalId: String): Result<List<Review>> {
        return try {
            val snapshot = reviewsCollection.where { "professionalId" equalTo professionalId }.get()
            val list = snapshot.documents.map { it.data<Review>().copy(id = it.id) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserReview(professionalId: String, userId: String): Result<Review?> {
        return try {
            val snapshot = reviewsCollection
                .where { "professionalId" equalTo professionalId }
                .where { "userId" equalTo userId }
                .get()
            Result.success(snapshot.documents.firstOrNull()?.data())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}