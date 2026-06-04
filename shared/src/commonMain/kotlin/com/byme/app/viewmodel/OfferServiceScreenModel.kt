package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.domain.model.Schedule
import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.CategoryRepositoryInterface
import com.byme.app.domain.repository.ScheduleRepositoryInterface
import com.byme.app.domain.repository.ServiceRepositoryInterface
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.ui.state.OfferServiceUiState
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OfferServiceScreenModel(
    private val categoryRepository: CategoryRepositoryInterface,
    private val serviceRepository: ServiceRepositoryInterface,
    private val scheduleRepository: ScheduleRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val auth: FirebaseAuth
) : ScreenModel {

    private val _uiState = MutableStateFlow(OfferServiceUiState())
    val uiState: StateFlow<OfferServiceUiState> = _uiState

    val dayOptions = listOf(
        "Lunes - Viernes",
        "Sábados",
        "Domingos y festivos"
    )

    val timeOptions = listOf(
        "6:00", "7:00", "8:00", "9:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00"
    )

    init {
        loadCategories()
    }

    private fun loadCategories() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.getCategories().fold(
                onSuccess = { categories ->
                    _uiState.update { it.copy(isLoading = false, categories = categories) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(selectedCategory = category) }
    fun onExperienceSelected(experience: String) = _uiState.update { it.copy(selectedExperience = experience) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onDaySelected(day: String) = _uiState.update { it.copy(selectedDay = day) }
    fun onStartTimeSelected(time: String) = _uiState.update { it.copy(selectedStartTime = time) }
    fun onEndTimeSelected(time: String) = _uiState.update { it.copy(selectedEndTime = time) }

    fun addService(name: String, description: String) {
        val newService = Service(name = name, description = description)
        _uiState.update { it.copy(services = it.services + newService) }
    }

    fun removeService(index: Int) {
        _uiState.update {
            it.copy(services = it.services.toMutableList().also { list -> list.removeAt(index) })
        }
    }

    fun addSchedule() {
        val state = _uiState.value
        if (state.selectedDay.isEmpty() || state.selectedStartTime.isEmpty() || state.selectedEndTime.isEmpty()) return
        val hours = "${state.selectedStartTime} - ${state.selectedEndTime}"
        val existingSchedule = state.schedules.find { it.day == state.selectedDay }

        val updatedSchedules = if (existingSchedule != null) {
            // El día ya existe — agregar jornada al mismo
            state.schedules.map { schedule ->
                if (schedule.day == state.selectedDay) {
                    schedule.copy(hours = "${schedule.hours}\n$hours")
                } else schedule
            }
        } else {
            // Día nuevo
            state.schedules + Schedule(day = state.selectedDay, hours = hours)
        }

        _uiState.update {
            it.copy(
                schedules = updatedSchedules,
                selectedDay = "",
                selectedStartTime = "",
                selectedEndTime = ""
            )
        }
    }

    fun removeSchedule(index: Int) {
        _uiState.update {
            it.copy(schedules = it.schedules.toMutableList().also { list -> list.removeAt(index) })
        }
    }

    fun saveProfile() {
        val userId = auth.currentUser?.uid ?: return
        val state = _uiState.value

        if (state.selectedCategory.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Selecciona una categoría") }
            return
        }
        if (state.selectedExperience.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Selecciona tu experiencia") }
            return
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            userRepository.getUser(userId).fold(
                onSuccess = { user ->
                    val updatedUser = user.copy(
                        isProfessional = true,
                        category = state.selectedCategory,
                        experience = state.selectedExperience,
                        description = state.description,
                        role = "professional",
                    )
                    userRepository.updateUser(updatedUser).fold(
                        onSuccess = {
                            // Guardar servicios y horarios
                            state.services.forEach { service ->
                                serviceRepository.addService(userId, service)
                            }
                            state.schedules.forEach { schedule ->
                                scheduleRepository.addSchedule(userId, schedule)
                            }
                            _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                        },
                        onFailure = { error ->
                            _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}