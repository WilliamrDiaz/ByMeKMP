package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Appointment
import com.byme.app.domain.repository.AppointmentRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where

class AppointmentRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : AppointmentRepositoryInterface {

    private val appointmentsCollection = firestore.collection("appointments")

    override suspend fun createAppointment(appointment: Appointment): Result<Unit> {
        return try {
            appointmentsCollection.add(appointment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserAppointments(userId: String): Result<List<Appointment>> {
        return try {
            val snapshot = appointmentsCollection
                .where { "userId" equalTo userId }
                .get()
            val list = snapshot.documents.map { it.data<Appointment>().copy(id = it.id) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfessionalAppointments(professionalId: String): Result<List<Appointment>> {
        return try {
            val snapshot = appointmentsCollection.where { "professionalId" equalTo professionalId }.get()
            val list = snapshot.documents.map { it.data<Appointment>().copy(id = it.id) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit> {
        return try {
            appointmentsCollection.document(appointmentId).update("status" to status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}