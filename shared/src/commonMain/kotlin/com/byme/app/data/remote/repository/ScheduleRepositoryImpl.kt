package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Schedule
import com.byme.app.domain.repository.ScheduleRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore

class ScheduleRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ScheduleRepositoryInterface {

    override suspend fun getSchedules(userId: String): Result<List<Schedule>> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("schedules").get()
            val schedules = snapshot.documents.map { it.data<Schedule>().copy(id = it.id) }
            Result.success(schedules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSchedule(userId: String, schedule: Schedule): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .collection("schedules").add(schedule)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSchedule(userId: String, scheduleId: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .collection("schedules").document(scheduleId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}