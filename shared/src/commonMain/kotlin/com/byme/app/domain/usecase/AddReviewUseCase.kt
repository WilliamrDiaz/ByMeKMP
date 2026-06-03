package com.byme.app.domain.usecase

import com.byme.app.domain.model.Review
import com.byme.app.domain.repository.ReviewRepositoryInterface

class AddReviewUseCase(
    private val repository: ReviewRepositoryInterface
) {
    suspend operator fun invoke(review: Review): Result<Unit> {
        return repository.addReview(review)
    }
}