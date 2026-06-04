package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.data.local.DraftManager
import com.byme.app.domain.model.Chat
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.byme.app.ui.state.ChatListUiState
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListScreenModel(
    private val chatRepository: ChatRepositoryInterface,
    private val draftManager: DraftManager,
    private val auth: FirebaseAuth
) : ScreenModel {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState

    init {
        loadChats()
    }

    private fun loadChats() {
        val userId = auth.currentUser?.uid ?: return
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Obtenemos el Flow de chats
                chatRepository.getChats(userId).collect { chats ->
                    // Cargar borradores para cada chat
                    val drafts = draftManager.getAllDrafts()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            chats = chats,
                            drafts = drafts
                        )
                    }
                    // Refrescamos los pendientes después de cargar los reales
                    refreshDrafts()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun refreshDrafts() {
        val drafts = draftManager.getAllDrafts()
        val existingChatIds = _uiState.value.chats.map { it.id }.toSet()

        // Crear chats pendientes para borradores que no tienen chat en Firestore
        val pendingChats = drafts.keys
            .filter { chatId -> chatId !in existingChatIds }
            .map { chatId ->
                Chat(
                    id = chatId,
                    professionalName = draftManager.getDraftName(chatId),
                    lastMessageTime = 0L,
                    lastMessage = drafts[chatId] ?: ""
                )
            }

        _uiState.update {
            it.copy(
                drafts = drafts,
                pendingChats = pendingChats
            )
        }
    }
}