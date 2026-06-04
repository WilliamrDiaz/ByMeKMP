package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.domain.model.Schedule
import com.byme.app.domain.model.Service
import com.byme.app.domain.repository.ScheduleRepositoryInterface
import com.byme.app.domain.repository.ServiceRepositoryInterface
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.ui.state.ProfessionalProfileUiState
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfessionalProfileScreenModel(
    private val userRepository: UserRepositoryInterface,
    private val serviceRepository: ServiceRepositoryInterface,
    private val scheduleRepository: ScheduleRepositoryInterface,
    private val auth: FirebaseAuth
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfessionalProfileUiState())
    val uiState: StateFlow<ProfessionalProfileUiState> = _uiState

    val dayOptions = listOf("Lunes - Viernes", "Sábados", "Domingos y festivos")
    val timeOptions = listOf(
        "6:00", "7:00", "8:00", "9:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00"
    )

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getUser(userId).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            name = user.name,
                            lastname = user.lastname,
                            description = user.description
                        )
                    }
                    launch { loadServices(userId) }
                    launch { loadSchedules(userId) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    private suspend fun loadServices(userId: String) {
        serviceRepository.getServices(userId).fold(
            onSuccess = { services ->
                _uiState.update { it.copy(services = services, isLoading = false) }
            },
            onFailure = { _uiState.update { it.copy(isLoading = false) } }
        )
    }

    private suspend fun loadSchedules(userId: String) {
        scheduleRepository.getSchedules(userId).fold(
            onSuccess = { schedules ->
                _uiState.update { it.copy(schedules = schedules) }
            },
            onFailure = { }
        )
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, hasChanges = true) }
    fun onLastnameChange(value: String) = _uiState.update { it.copy(lastname = value, hasChanges = true) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value, hasChanges = true) }
    fun onDaySelected(day: String) = _uiState.update { it.copy(selectedDay = day) }
    fun onStartTimeSelected(time: String) = _uiState.update { it.copy(selectedStartTime = time) }
    fun onEndTimeSelected(time: String) = _uiState.update { it.copy(selectedEndTime = time) }

    fun addService(name: String, description: String) {
        val newService = Service(name = name, description = description)
        _uiState.update { it.copy(services = it.services + newService, hasChanges = true) }
    }

    fun removeService(service: Service) {
        _uiState.update { it.copy(services = it.services - service, hasChanges = true) }
    }

    fun addSchedule() {
        val state = _uiState.value
        if (state.selectedDay.isEmpty() || state.selectedStartTime.isEmpty() || state.selectedEndTime.isEmpty()) return
        val hours = "${state.selectedStartTime} - ${state.selectedEndTime}"
        val existingSchedule = state.schedules.find { it.day == state.selectedDay }
        val updatedSchedules = if (existingSchedule != null) {
            state.schedules.map { schedule ->
                if (schedule.day == state.selectedDay) {
                    schedule.copy(hours = "${schedule.hours}\n$hours")
                } else schedule
            }
        } else {
            state.schedules + Schedule(day = state.selectedDay, hours = hours)
        }
        _uiState.update {
            it.copy(schedules = updatedSchedules, selectedDay = "", selectedStartTime = "", selectedEndTime = "", hasChanges = true)
        }
    }

    fun removeSchedule(schedule: Schedule) {
        _uiState.update { it.copy(schedules = it.schedules - schedule, hasChanges = true) }
    }

    fun saveProfile() {
        val userId = auth.currentUser?.uid ?: return
        val state = _uiState.value
        val user = state.user ?: return

        screenModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val updatedUser = user.copy(name = state.name, lastname = state.lastname, description = state.description)
            userRepository.updateUser(updatedUser).fold(
                onSuccess = {
                    state.services.filter { it.id.isEmpty() }.forEach { serviceRepository.addService(userId, it) }
                    state.schedules.filter { it.id.isEmpty() }.forEach { scheduleRepository.addSchedule(userId, it) }
                    _uiState.update { it.copy(isSaving = false, isSuccess = true, hasChanges = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}