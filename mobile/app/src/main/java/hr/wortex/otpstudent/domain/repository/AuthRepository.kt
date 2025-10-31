package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.repository.interfaces.IAuthRepository

class AuthRepository(private val remoteDataSource: AuthRemoteDataSource): IAuthRepository {
    override suspend fun login(email: String, hashPassword: String): UserLogin {
        val dto = remoteDataSource.login(LoginDto(email,hashPassword))

        return UserLogin(
            email = dto.email!!,
            responseMessage = dto.success ?: ""
        )
    }
}