package com.nimmaguru.domain.repository

import com.nimmaguru.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(sessionId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(sessionId: String, text: String, languageCode: String): Result<Unit>
}
