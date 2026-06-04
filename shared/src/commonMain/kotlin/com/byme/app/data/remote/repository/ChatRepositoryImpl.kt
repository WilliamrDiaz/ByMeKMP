package com.byme.app.data.remote.repository

import com.byme.app.domain.model.Chat
import com.byme.app.domain.model.Message
import com.byme.app.domain.repository.ChatRepositoryInterface
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ChatRepositoryInterface {

    private val chatsCollection = firestore.collection("chats")

    override suspend fun getOrCreateChat(
        userId: String,
        professionalId: String,
        userName: String,
        professionalName: String
    ): Result<Chat> {
        return try {
            val chatId = "${userId}_${professionalId}"
            val docRef = chatsCollection.document(chatId)
            val doc = docRef.get()

            if (doc.exists) {
                Result.success(doc.data())
            } else {
                val newChat = Chat(
                    id = chatId,
                    userId = userId,
                    professionalId = professionalId,
                    userName = userName,
                    professionalName = professionalName,
                    lastMessageTime = 0L // En KMP lo inicializamos así
                )
                docRef.set(newChat)
                Result.success(newChat)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createChat(chat: Chat): Result<String> {
        return try {
            val docRef = chatsCollection.add(chat)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChats(userId: String): Flow<List<Chat>> {
        // En GitLive usamos .snapshots para obtener un Flow en tiempo real
        val flowUser = chatsCollection.where { "userId" equalTo userId }.snapshots
        val flowProf = chatsCollection.where { "professionalId" equalTo userId }.snapshots

        // Combinamos ambos flujos donde soy cliente y donde soy profesional
        return combine(flowUser, flowProf) { snapUser, snapProf ->
            val listUser = snapUser.documents.map { it.data<Chat>().copy(id = it.id) }
            val listProf = snapProf.documents.map { it.data<Chat>().copy(id = it.id) }

            (listUser + listProf).sortedByDescending { it.lastMessageTime }
        }
    }

    override suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            chatsCollection.document(chatId).collection("messages").add(message)

            // Actualizar el último mensaje del chat
            chatsCollection.document(chatId).update(
                "lastMessage" to message.text,
                "lastMessageTime" to message.timestamp
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessages(chatId: String): Flow<List<Message>> {
        return chatsCollection.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Direction.ASCENDING)
            .snapshots
            .map { snap ->
                snap.documents.map { it.data<Message>().copy(id = it.id) }
            }
    }

    override suspend fun markAsRead(chatId: String, userId: String): Result<Unit> {
        return try {
            chatsCollection.document(chatId).update("unreadCount" to 0)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}