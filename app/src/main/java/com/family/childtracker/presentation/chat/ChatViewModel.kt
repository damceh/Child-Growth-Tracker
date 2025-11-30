package com.family.childtracker.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.ChatMessage
import com.family.childtracker.domain.repository.ChatMessageRepository
import com.family.childtracker.domain.usecase.SendChatMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatMessageRepository: ChatMessageRepository,
    private val sendChatMessageUseCase: SendChatMessageUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        loadMessages()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            chatMessageRepository.getAllMessages().collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            }
        }
    }
    
    fun onMessageChanged(message: String) {
        _uiState.value = _uiState.value.copy(inputMessage = message)
    }
    
    fun sendMessage(selectedChildId: String? = null) {
        val message = _uiState.value.inputMessage.trim()
        if (message.isBlank() || _uiState.value.isSending) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSending = true,
                inputMessage = "",
                error = null
            )
            
            when (val result = sendChatMessageUseCase(message, selectedChildId)) {
                is SendChatMessageUseCase.Result.Success -> {
                    _uiState.value = _uiState.value.copy(isSending = false)
                }
                is SendChatMessageUseCase.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            chatMessageRepository.deleteAllMessages()
        }
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputMessage: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
