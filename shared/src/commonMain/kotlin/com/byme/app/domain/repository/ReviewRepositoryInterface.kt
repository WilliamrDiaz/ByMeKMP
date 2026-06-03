package com.byme.app.domain.repository

import com.byme.app.domain.model.Review

interface ReviewRepositoryInterface {
    suspend fun addReview(review: Review): Result<Unit>
    suspend fun getReviews(professionalId: String): Result<List<Review>>
    suspend fun getUserReview(professionalId: String, userId: String): Result<Review?>
}