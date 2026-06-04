package com.byme.app.ui.state

import com.byme.app.domain.model.Category
import com.byme.app.domain.model.Schedule
import com.byme.app.domain.model.Service

data class OfferServiceUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "",
    val selectedExperience: String = "",
    val description: String = "",
    val services: List<Service> = emptyList(),
    val schedules: List<Schedule> = emptyList(),
    val selectedDay: String = "",
    val selectedStartTime: String = "",
    val selectedEndTime: String = "",
    val errorMessage: String? = null
)