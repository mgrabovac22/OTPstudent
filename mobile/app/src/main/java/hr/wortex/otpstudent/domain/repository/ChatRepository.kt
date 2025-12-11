package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.interfaces.IChatRemoteDataSource
import hr.wortex.otpstudent.domain.repository.interfaces.IChatRepository
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository

class ChatRepository(
    private val remoteDataSource: IChatRemoteDataSource,
    private val userRepository: IUserRepository
) : IChatRepository {

    override suspend fun sendMessage(message: String, history: List<Map<String, String>>): String {
        val user = userRepository.getCurrentUser()
        return remoteDataSource.sendMessage(message, history, user.email)
    }
}