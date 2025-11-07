package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.repository.interfaces.IAuthRepository

class AuthRepository(
    private val remote: AuthRemoteDataSource,
    private val storage: TokenStorage
) : IAuthRepository {

    override suspend fun login(email: String, hashPassword: String): UserLogin {
        val dto = remote.login(LoginDto(email, hashPassword))

        storage.saveTokens(dto.accessToken!!, dto.refreshToken!!)

        return UserLogin(
            email = dto.email!!,
            responseMessage = dto.success ?: ""
        )
    }

    suspend fun hasSavedTokens(): Boolean {
        return !storage.getAccessToken().isNullOrBlank() || !storage.getRefreshToken().isNullOrBlank()
    }
}
