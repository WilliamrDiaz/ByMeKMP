package com.byme.app.domain.usecase

import com.byme.app.domain.model.Review
import com.byme.app.domain.repository.ReviewRepositoryInterface

class GetReviewsUseCase(
    private val repository: ReviewRepositoryInterface
) {
    suspend operator fun invoke(professionalId: String): Result<List<Review>> {
        return repository.getReviews(professionalId)
    }
}