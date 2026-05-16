package com.nimmaguru.domain.model

data class ChatMessage(
    val id: String,
    val text: String,
    val fromUser: Boolean,
    val timestampMillis: Long,
)
