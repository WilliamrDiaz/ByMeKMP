package com.byme.app.domain.usecase

import com.byme.app.domain.model.Schedule
import com.byme.app.domain.repository.ScheduleRepositoryInterface

class AddScheduleUseCase(
    private val repository: ScheduleRepositoryInterface
) {
    suspend operator fun invoke(userId: String, schedule: Schedule): Result<Unit> {
        return repository.addSchedule(userId, schedule)
    }
}