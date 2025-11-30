package com.family.childtracker.presentation.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.local.preferences.SecurePreferences
import com.family.childtracker.data.remote.api.OpenRouterClient
import com.family.childtracker.data.repository.AppSettingsRepositoryImpl
import com.family.childtracker.data.repository.ChatMessageRepositoryImpl
import com.family.childtracker.data.repository.ChildProfileRepositoryImpl
import com.family.childtracker.data.repository.MilestoneRepositoryImpl
import com.family.childtracker.data.repository.OpenRouterRepositoryImpl
import com.family.childtracker.domain.usecase.SendChatMessageUseCase

class ChatViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(context)
            val securePreferences = SecurePreferences(context)
            
            val chatMessageRepository = ChatMessageRepositoryImpl(
                chatMessageDao = database.chatMessageDao()
            )
            
            val appSettingsRepository = AppSettingsRepositoryImpl(
                appSettingsDao = database.appSettingsDao(),
                securePreferences = securePreferences
            )
            
            val childProfileRepository = ChildProfileRepositoryImpl(
                childProfileDao = database.childProfileDao()
            )
            
            val milestoneRepository = MilestoneRepositoryImpl(
                milestoneDao = database.milestoneDao()
            )
            
            // Create OpenRouter API client
            val apiService = OpenRouterClient.create(
                apiKeyProvider = { securePreferences.getApiKey() }
            )
            
            val openRouterRepository = OpenRouterRepositoryImpl(apiService)
            
            val sendChatMessageUseCase = SendChatMessageUseCase(
                chatMessageRepository = chatMessageRepository,
                openRouterRepository = openRouterRepository,
                appSettingsRepository = appSettingsRepository,
                childProfileRepository = childProfileRepository,
                milestoneRepository = milestoneRepository
            )
            
            return ChatViewModel(
                chatMessageRepository = chatMessageRepository,
                sendChatMessageUseCase = sendChatMessageUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
