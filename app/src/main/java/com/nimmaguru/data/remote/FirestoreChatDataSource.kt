package com.nimmaguru.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.nimmaguru.domain.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreChatDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    private fun messagesCol(uid: String, sessionId: String) =
        firestore.collection(FirestoreUserDataSource.COLLECTION_USERS)
            .document(uid)
            .collection("chat_sessions")
            .document(sessionId)
            .collection("messages")

    fun observeMessages(uid: String, sessionId: String): Flow<List<ChatMessage>> = callbackFlow {
        var registration: ListenerRegistration? = null
        val query = messagesCol(uid, sessionId)
            .orderBy("timestampMillis", Query.Direction.ASCENDING)
            .limit(200)
        registration = query.addSnapshotListener { snap, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snap?.documents?.mapNotNull { doc ->
                val text = doc.getString("text") ?: return@mapNotNull null
                ChatMessage(
                    id = doc.id,
                    text = text,
                    fromUser = doc.getBoolean("fromUser") ?: true,
                    timestampMillis = doc.getLong("timestampMillis") ?: 0L,
                )
            }.orEmpty()
            trySend(list)
        }
        awaitClose { registration?.remove() }
    }

    suspend fun addMessage(uid: String, sessionId: String, message: ChatMessage) {
        val data = mapOf(
            "text" to message.text,
            "fromUser" to message.fromUser,
            "timestampMillis" to message.timestampMillis,
        )
        messagesCol(uid, sessionId).document(message.id).set(data).await()
    }
}
