package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.domain.usecase.GetUserUseCase
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserTypeScreenModel(
    private val getUserUseCase: GetUserUseCase,
    private val auth: FirebaseAuth
) : ScreenModel {

    private val _isProfessional = MutableStateFlow<Boolean?>(null)
    val isProfessional: StateFlow<Boolean?> = _isProfessional

    init {
        checkUserType()
    }

    private fun checkUserType() {
        val userId = auth.currentUser?.uid ?: return
        screenModelScope.launch {
            getUserUseCase(userId).fold(
                onSuccess = { user ->
                    _isProfessional.value = user.isProfessional
                },
                onFailure = {
                    _isProfessional.value = false
                }
            )
        }
    }
}