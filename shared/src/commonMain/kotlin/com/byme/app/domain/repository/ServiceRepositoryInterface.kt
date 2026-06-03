package com.byme.app.domain.repository

import com.byme.app.domain.model.Service

interface ServiceRepositoryInterface {
    suspend fun getServices(userId: String): Result<List<Service>>
    suspend fun addService(userId: String, service: Service): Result<Unit>
    suspend fun deleteService(userId: String, serviceId: String): Result<Unit>
}