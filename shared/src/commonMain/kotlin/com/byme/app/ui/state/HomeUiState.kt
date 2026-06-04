package com.byme.app.ui.state

import com.byme.app.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val professionals: List<User> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)