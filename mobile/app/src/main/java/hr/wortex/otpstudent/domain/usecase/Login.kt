package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.repository.interfaces.IAuthRepository

class Login (private val authRepository: IAuthRepository) {
    suspend operator fun invoke(email: String, hashPassword: String): UserLogin {
        return authRepository.login(email,hashPassword)
    }
}