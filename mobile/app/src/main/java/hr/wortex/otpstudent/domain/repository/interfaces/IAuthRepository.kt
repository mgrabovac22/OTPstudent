package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.UserLogin

interface IAuthRepository {
    suspend fun login(email: String, hashPassword: String): UserLogin
}