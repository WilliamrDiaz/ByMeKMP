package com.byme.app.domain.repository

import com.byme.app.domain.model.Schedule

interface ScheduleRepositoryInterface {
    suspend fun getSchedules(userId: String): Result<List<Schedule>>
    suspend fun addSchedule(userId: String, schedule: Schedule): Result<Unit>
    suspend fun deleteSchedule(userId: String, scheduleId: String): Result<Unit>
}