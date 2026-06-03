package com.byme.app.domain.usecase


import com.byme.app.domain.model.Schedule
import com.byme.app.domain.repository.ScheduleRepositoryInterface

class GetSchedulesUseCase(
    private val repository: ScheduleRepositoryInterface
) {
    suspend operator fun invoke(userId: String): Result<List<Schedule>> {
        return repository.getSchedules(userId)
    }
}