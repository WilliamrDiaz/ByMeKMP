package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.domain.usecase.GetUserUseCase
import com.byme.app.domain.usecase.UpdateUserUseCase
import com.byme.app.ui.state.ProfileUiState
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val auth: FirebaseAuth // Inyectamos la instancia compartida
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserUseCase(userId).fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            name = user.name,
                            lastname = user.lastname,
                            phone = user.phone
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onLastnameChange(value: String) = _uiState.update { it.copy(lastname = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }

    fun saveProfile() {
        val user = _uiState.value.user ?: return
        screenModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updatedUser = user.copy(
                name = _uiState.value.name,
                lastname = _uiState.value.lastname,
                phone = _uiState.value.phone
            )
            updateUserUseCase(updatedUser).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
}