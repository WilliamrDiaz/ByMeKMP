package com.byme.app.ui.state

import com.byme.app.domain.model.Schedule
import com.byme.app.domain.model.Service
import com.byme.app.domain.model.User

data class ProfessionalProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val name: String = "",
    val lastname: String = "",
    val description: String = "",
    val services: List<Service> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
    val selectedDay: String = "",
    val selectedStartTime: String = "",
    val selectedEndTime: String = "",
    val errorMessage: String? = null,
    val hasChanges: Boolean = false
)