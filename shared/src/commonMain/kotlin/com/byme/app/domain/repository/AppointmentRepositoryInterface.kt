package com.byme.app.domain.repository

import com.byme.app.domain.model.Appointment

interface AppointmentRepositoryInterface {
    suspend fun createAppointment(appointment: Appointment): Result<Unit>
    suspend fun getUserAppointments(userId: String): Result<List<Appointment>>
    suspend fun getProfessionalAppointments(professionalId: String): Result<List<Appointment>>
    suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit>
}