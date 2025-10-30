package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository

class GetUser(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(): User {
        return userRepository.getCurrentUser()
    }
}