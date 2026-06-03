package com.byme.app.domain.usecase

import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.ServiceRepositoryInterface

class GetServicesUseCase(
    private val repository: ServiceRepositoryInterface
) {
    suspend operator fun invoke(userId: String): Result<List<Service>> {
        return repository.getServices(userId)
    }
}