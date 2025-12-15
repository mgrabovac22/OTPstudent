package hr.wortex.otpstudent.domain.repository.interfaces

interface IChatRepository {
    suspend fun sendMessage(message: String, history: List<Map<String, String>>): String
}