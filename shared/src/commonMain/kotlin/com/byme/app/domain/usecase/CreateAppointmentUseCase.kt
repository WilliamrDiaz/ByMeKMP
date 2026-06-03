package com.byme.app.domain.usecase

import com.byme.app.domain.model.Appointment
import com.byme.app.domain.repository.AppointmentRepositoryInterface

class CreateAppointmentUseCase(
    private val repository: AppointmentRepositoryInterface
) {
    suspend operator fun invoke(appointment: Appointment): Result<Unit> {
        return repository.createAppointment(appointment)
    }
}