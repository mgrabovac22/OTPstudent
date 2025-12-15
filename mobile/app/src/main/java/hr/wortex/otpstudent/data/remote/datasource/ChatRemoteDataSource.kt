package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IChatRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.ChatRequestDto

class ChatRemoteDataSource (
    private val apiService: IOtpApiService
) : IChatRemoteDataSource {
    override suspend fun sendMessage(message: String, history: List<Map<String, String>>, email: String?): String {
        val request = ChatRequestDto(message = message, history = history, email = email)
        return apiService.chat(request).answer
    }
}