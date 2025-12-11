package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.interfaces.IChatRepository

class SendChatMessage(private val chatRepository: IChatRepository) {

    suspend operator fun invoke(message: String, history: List<Map<String, String>>): String {
        return chatRepository.sendMessage(message, history)
    }
}