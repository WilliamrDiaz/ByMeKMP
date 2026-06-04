package com.byme.app.data.local

class DraftManager {

    // Almacena chatId -> Texto del mensaje
    private val drafts = mutableMapOf<String, String>()
    // Almacena chatId -> Nombre del profesional (para mostrar en la lista de chats pendientes)
    private val draftNames = mutableMapOf<String, String>()

    fun saveDraft(chatId: String, text: String, professionalName: String = "") {
        if (text.isBlank()) {
            drafts.remove(chatId)
            draftNames.remove(chatId)
        } else {
            drafts[chatId] = text
            if (professionalName.isNotEmpty()) {
                draftNames[chatId] = professionalName
            }
        }
    }

    fun getDraft(chatId: String): String = drafts[chatId] ?: ""

    fun getDraftName(chatId: String): String = draftNames[chatId] ?: ""

    fun clearDraft(chatId: String) {
        drafts.remove(chatId)
        draftNames.remove(chatId)
    }

    fun getAllDrafts(): Map<String, String> = drafts.toMap()
}