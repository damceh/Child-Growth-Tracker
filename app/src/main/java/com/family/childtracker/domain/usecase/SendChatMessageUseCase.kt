package com.family.childtracker.domain.usecase

import com.family.childtracker.data.remote.model.Message
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.domain.model.ChatMessage
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.MessageRole
import com.family.childtracker.domain.repository.AppSettingsRepository
import com.family.childtracker.domain.repository.ChatMessageRepository
import com.family.childtracker.domain.repository.ChildProfileRepository
import com.family.childtracker.domain.repository.MilestoneRepository
import com.family.childtracker.domain.repository.OpenRouterRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.Period
import java.util.UUID

/**
 * Use case for sending chat messages to AI assistant with child context
 */
class SendChatMessageUseCase(
    private val chatMessageRepository: ChatMessageRepository,
    private val openRouterRepository: OpenRouterRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val childProfileRepository: ChildProfileRepository,
    private val milestoneRepository: MilestoneRepository
) {
    
    companion object {
        private const val CONTEXT_MESSAGE_LIMIT = 10
        private const val DEFAULT_MODEL = "openai/gpt-3.5-turbo"
    }
    
    suspend operator fun invoke(userMessage: String, selectedChildId: String?): Result {
        // Validate user message
        if (userMessage.isBlank()) {
            return Result.Error("Message cannot be empty")
        }
        
        // Check if API key is configured
        val settings = appSettingsRepository.getSettingsOnce()
        if (settings?.openRouterApiKey.isNullOrBlank()) {
            return Result.Error("API key not configured. Please configure your OpenRouter API key in Settings.")
        }
        
        // Save user message
        val userChatMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = userMessage,
            timestamp = Instant.now()
        )
        chatMessageRepository.insertMessage(userChatMessage)
        
        // Build conversation context
        val recentMessages = chatMessageRepository.getRecentMessages(CONTEXT_MESSAGE_LIMIT)
        val conversationMessages = buildConversationMessages(recentMessages, selectedChildId)
        
        // Get selected model or use default
        val model = settings.selectedModel?.takeIf { it.isNotBlank() } ?: DEFAULT_MODEL
        
        // Send to OpenRouter API
        return when (val apiResult = openRouterRepository.chatCompletion(
            model = model,
            messages = conversationMessages,
            temperature = 0.7f
        )) {
            is ApiResult.Success -> {
                // Save assistant response
                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = MessageRole.ASSISTANT,
                    content = apiResult.data,
                    timestamp = Instant.now()
                )
                chatMessageRepository.insertMessage(assistantMessage)
                
                Result.Success(assistantMessage)
            }
            is ApiResult.Error -> {
                Result.Error(apiResult.exception.message)
            }
        }
    }
    
    private suspend fun buildConversationMessages(
        recentMessages: List<ChatMessage>,
        selectedChildId: String?
    ): List<Message> {
        val messages = mutableListOf<Message>()
        
        // Add system prompt with child context
        val systemPrompt = buildSystemPrompt(selectedChildId)
        messages.add(Message(role = "system", content = systemPrompt))
        
        // Add recent conversation history (in chronological order)
        recentMessages.reversed().forEach { chatMessage ->
            if (chatMessage.role != MessageRole.SYSTEM) {
                messages.add(
                    Message(
                        role = when (chatMessage.role) {
                            MessageRole.USER -> "user"
                            MessageRole.ASSISTANT -> "assistant"
                            MessageRole.SYSTEM -> "system"
                        },
                        content = chatMessage.content
                    )
                )
            }
        }
        
        return messages
    }
    
    private suspend fun buildSystemPrompt(selectedChildId: String?): String {
        val basePrompt = """
            You are a helpful parenting assistant for the Child Growth Tracker app. 
            You provide supportive, evidence-based advice on child development, growth, milestones, and parenting.
            Always be encouraging and empathetic. If asked about medical concerns, remind parents to consult healthcare professionals.
        """.trimIndent()
        
        if (selectedChildId == null) {
            return basePrompt
        }
        
        // Get child profile
        val child = childProfileRepository.getProfileById(selectedChildId) ?: return basePrompt
        
        // Calculate child's age
        val age = Period.between(child.dateOfBirth, java.time.LocalDate.now())
        val ageDescription = when {
            age.years > 0 -> "${age.years} year${if (age.years > 1) "s" else ""} and ${age.months} month${if (age.months != 1) "s" else ""}"
            age.months > 0 -> "${age.months} month${if (age.months != 1) "s" else ""}"
            else -> "${age.days} day${if (age.days != 1) "s" else ""}"
        }
        
        // Get recent milestones
        val recentMilestones = try {
            milestoneRepository.getMilestonesByChildId(selectedChildId).first().take(3)
        } catch (e: Exception) {
            emptyList()
        }
        
        val milestonesText = if (recentMilestones.isNotEmpty()) {
            "\n\nRecent milestones:\n" + recentMilestones.joinToString("\n") { 
                "- ${it.description} (${it.category}, achieved on ${it.achievementDate})"
            }
        } else {
            ""
        }
        
        return """
            $basePrompt
            
            Context: The parent is tracking ${child.name}, a ${child.gender.name.lowercase()} child who is $ageDescription old.$milestonesText
            
            Use this context to provide personalized, age-appropriate advice.
        """.trimIndent()
    }
    
    sealed class Result {
        data class Success(val message: ChatMessage) : Result()
        data class Error(val message: String) : Result()
    }
}
