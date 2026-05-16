package com.nimmaguru.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.nimmaguru.data.remote.FirestoreChatDataSource
import com.nimmaguru.domain.model.ChatMessage
import com.nimmaguru.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val chatDataSource: FirestoreChatDataSource,
) : ChatRepository {

    override fun observeMessages(sessionId: String): Flow<List<ChatMessage>> =
        auth.uidFlow().flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else chatDataSource.observeMessages(uid, sessionId)
        }

    override suspend fun sendMessage(sessionId: String, text: String, languageCode: String): Result<Unit> =
        runCatching {
            val uid = auth.currentUser?.uid ?: error("Not signed in")
            val trimmed = text.trim()
            if (trimmed.isEmpty()) return@runCatching
            val now = System.currentTimeMillis()
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = trimmed,
                fromUser = true,
                timestampMillis = now,
            )
            chatDataSource.addMessage(uid, sessionId, userMessage)
            delay(450)
            val reply = buildBotReply(trimmed, languageCode)
            val botMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = reply,
                fromUser = false,
                timestampMillis = System.currentTimeMillis(),
            )
            chatDataSource.addMessage(uid, sessionId, botMessage)
        }

    private fun buildBotReply(userText: String, languageCode: String): String {
        val lower = userText.lowercase()
        return if (languageCode.startsWith("kn")) {
            when {
                lower.contains("hello") || lower.contains("namaskara") || lower.contains("namaste") ->
                    "ನಮಸ್ಕಾರ! ನಾನು ನಿಮ್ಮ ಗುರು ಸಹಾಯಕ. ನೀವು ಯಾವ ವಿಷಯದಲ್ಲಿ ಮಾರ್ಗದರ್ಶನ ಬಯಸುತ್ತೀರಿ?"
                lower.contains("guru") || lower.contains("teacher") ->
                    "ನಿಮಗೆ ಬೇಕಾದ ಗುರುವನ್ನು ಹುಡುಕಲು 'ಗುರು ಹುಡುಕು' ಪುಟಕ್ಕೆ ಹೋಗಿ. ಅನುಭವ, ನಗರ ಮತ್ತು ವಿಷಯದ ಮೂಲಕ ಶೋಧಿಸಬಹುದು."
                lower.contains("thank") || lower.contains("ಧನ್ಯವಾದ") ->
                    "ಧನ್ಯವಾದಗಳು. ನಿಮ್ಮ ಕಲಿಕೆಯ ಪ್ರಯಾಣದಲ್ಲಿ ನಾವು ಯಾವಾಗಲೂ ಇಲ್ಲಿದ್ದೇವೆ."
                else ->
                    "ನಾನು ಈಗ ಒಂದು ಸರಳ ಸಹಾಯಕ. ನಿಮ್ಮ ಪ್ರಶ್ನೆಯನ್ನು ಸಂಕ್ಷಿಪ್ತವಾಗಿ ಬರೆಯಿರಿ, ಅಥವಾ ಗುರು ಹುಡುಕಾಟ ಅಥವಾ ನಿಮ್ಮ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್ ಬಗ್ಗೆ ಕೇಳಿ."
            }
        } else {
            when {
                lower.contains("hello") || lower.contains("hi") ->
                    "Hello! I am your Nimma Guru assistant. How can I guide you today?"
                lower.contains("guru") || lower.contains("teacher") ->
                    "Use Guru Search to filter mentors by city, subject, and experience. Your matches update live from Firestore."
                lower.contains("thank") ->
                    "You are welcome. Wishing you calm and confident learning."
                else ->
                    "I am a simple on-device style helper for now. Ask about the student or guru dashboard, finding a guru, or offline mode — Firestore keeps your chats in sync when you reconnect."
            }
        }
    }

    private fun FirebaseAuth.uidFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        addAuthStateListener(listener)
        awaitClose { removeAuthStateListener(listener) }
    }.distinctUntilChanged()
}
