package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatMessageRepository {
    fun getAllMessages(): Flow<List<ChatMessage>>
    suspend fun getRecentMessages(limit: Int): List<ChatMessage>
    suspend fun getMessageById(id: String): ChatMessage?
    suspend fun insertMessage(message: ChatMessage)
    suspend fun insertMessages(messages: List<ChatMessage>)
    suspend fun deleteMessage(message: ChatMessage)
    suspend fun deleteMessageById(id: String)
    suspend fun deleteAllMessages()
}
