package com.family.childtracker.domain.model

import java.time.Instant

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Instant
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}
