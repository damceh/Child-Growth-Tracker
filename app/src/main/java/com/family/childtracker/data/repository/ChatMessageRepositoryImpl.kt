package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.ChatMessageDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.ChatMessage
import com.family.childtracker.domain.repository.ChatMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatMessageRepositoryImpl(
    private val chatMessageDao: ChatMessageDao
) : ChatMessageRepository {

    override fun getAllMessages(): Flow<List<ChatMessage>> {
        return chatMessageDao.getAllMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRecentMessages(limit: Int): List<ChatMessage> {
        return chatMessageDao.getRecentMessages(limit).map { it.toDomain() }
    }

    override suspend fun getMessageById(id: String): ChatMessage? {
        return chatMessageDao.getMessageById(id)?.toDomain()
    }

    override suspend fun insertMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message.toEntity())
    }

    override suspend fun insertMessages(messages: List<ChatMessage>) {
        chatMessageDao.insertMessages(messages.map { it.toEntity() })
    }

    override suspend fun deleteMessage(message: ChatMessage) {
        chatMessageDao.deleteMessage(message.toEntity())
    }

    override suspend fun deleteMessageById(id: String) {
        chatMessageDao.deleteMessageById(id)
    }

    override suspend fun deleteAllMessages() {
        chatMessageDao.deleteAllMessages()
    }
}
