package com.byme.app.ui.state

import com.byme.app.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val user: User? = null,
    val name: String = "",
    val lastname: String = "",
    val phone: String = "",
    val errorMessage: String? = null
)