package com.byme.app.domain.usecase

import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.ServiceRepositoryInterface

class AddServiceUseCase(
    private val repository: ServiceRepositoryInterface
) {
    suspend operator fun invoke(userId: String, service: Service): Result<Unit> {
        return repository.addService(userId, service)
    }
}