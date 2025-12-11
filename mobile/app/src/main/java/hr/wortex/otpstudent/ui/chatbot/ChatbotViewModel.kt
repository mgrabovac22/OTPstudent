package hr.wortex.otpstudent.ui.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.ChatMessage
import hr.wortex.otpstudent.domain.usecase.SendChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val sendChatMessage: SendChatMessage
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(content = text, isUser = true)
        _messages.value += userMsg

        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val currentMessages = _messages.value
                val history = currentMessages
                    .dropLast(1)
                    .takeLast(10)
                    .map {
                        mapOf(
                            "role" to if (it.isUser) "user" else "assistant",
                            "content" to it.content
                        )
                    }

                val responseText = sendChatMessage(text, history)

                val botMsg = ChatMessage(content = responseText, isUser = false)
                _messages.value += botMsg

            } catch (e: Exception) {
                val errorMsg = ChatMessage(content = "Greška: ${e.message}", isUser = false)
                _messages.value += errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }
}
