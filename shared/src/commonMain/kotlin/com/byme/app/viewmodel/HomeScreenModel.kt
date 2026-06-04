package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.domain.usecase.GetProfessionalsUseCase
import com.byme.app.domain.usecase.SearchProfessionalsUseCase
import com.byme.app.ui.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val getProfessionalsUseCase: GetProfessionalsUseCase,
    private val searchProfessionalsUseCase: SearchProfessionalsUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProfessionals()
    }

    fun loadProfessionals() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getProfessionalsUseCase().fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, professionals = list) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        screenModelScope.launch {
            if (query.isEmpty()) {
                loadProfessionals()
            } else {
                _uiState.update { it.copy(isLoading = true) }
                searchProfessionalsUseCase(query).fold(
                    onSuccess = { list ->
                        _uiState.update { it.copy(isLoading = false, professionals = list) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                )
            }
        }
    }
}