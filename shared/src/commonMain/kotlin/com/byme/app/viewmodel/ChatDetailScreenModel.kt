package com.byme.app.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.byme.app.data.local.DraftManager
import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface
import com.byme.app.domain.repository.UserRepositoryInterface
import com.byme.app.ui.state.ChatDetailUiState
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FieldValue.Companion.serverTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class ChatDetailScreenModel(
    private val chatRepository: ChatRepositoryInterface,
    private val userRepository: UserRepositoryInterface,
    private val draftManager: DraftManager,
    private val auth: FirebaseAuth
) : ScreenModel {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState

    private var professionalId: String = ""
    private var chatExists: Boolean = false

    fun loadChat(chatId: String, professionalName: String) {
        // Extraer professionalId del chatId (formato: userId_professionalId)
        professionalId = chatId.substringAfter("_")

        // Restaurar borrador si existe
        val draft = draftManager.getDraft(chatId)

        _uiState.update {
            it.copy(
                chatId = chatId,
                professionalName = professionalName,
                messageText = draft
            )
        }

        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Escuchar mensajes en tiempo real
                chatRepository.getMessages(chatId).collect { messages ->
                    chatExists = messages.isNotEmpty()
                    _uiState.update { it.copy(isLoading = false, messages = messages) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, messages = emptyList()) }
            }
        }
    }

    fun onMessageTextChange(text: String) {
        _uiState.update { it.copy(messageText = text) }
        // Guardar borrador en local mientras escribes
        draftManager.saveDraft(
            _uiState.value.chatId,
            text,
            _uiState.value.professionalName
        )
    }

    fun sendMessage() {
        val state = _uiState.value
        if (state.messageText.isBlank()) return
        val currentUser = auth.currentUser ?: return

        screenModelScope.launch {
            val currentTime = Clock.System.now().toEpochMilliseconds()
            val message = Message(
                senderId = currentUser.uid,
                text = state.messageText.trim(),
                timestamp = currentTime,
            )

            // En KMP usamos tiempo local si el server timestamp falla
            val finalMessage = if (message.timestamp == 0L) message.copy(timestamp = Clock.System.now().toEpochMilliseconds()) else message

            // Si el chat no existe en la nube, lo creamos primero
            if (!chatExists) {
                val userId = currentUser.uid
                userRepository.getUser(userId).onSuccess { currentUserData ->
                    val userName = "${currentUserData.name} ${currentUserData.lastname}"
                    userRepository.getUser(professionalId).onSuccess { professional ->
                        val profName = "${professional.name} ${professional.lastname}"
                        chatRepository.getOrCreateChat(
                            userId = userId,
                            professionalId = professionalId,
                            userName = userName,
                            professionalName = profName
                        )
                        chatExists = true
                    }
                }
            }

            // Enviar mensaje a la nube
            chatRepository.sendMessage(state.chatId, finalMessage)

            // Limpiar borrador al enviar
            draftManager.clearDraft(state.chatId)
            _uiState.update { it.copy(messageText = "") }
        }
    }

    fun markAsRead() {
        val userId = auth.currentUser?.uid ?: return
        screenModelScope.launch {
            chatRepository.markAsRead(_uiState.value.chatId, userId)
        }
    }
}