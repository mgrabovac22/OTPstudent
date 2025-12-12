package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.RegisterDto
import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.model.UserRegister
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

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        hashPassword: String,
        yearOfStudy: Number,
        areaOfStudy: String,
        dateOfStudy: String,
        higherEducationBodyID: Int
    ): UserRegister {
        val dto = remote.register(RegisterDto(
            firstName,
            lastName,
            email,
            hashPassword,
            yearOfStudy,
            areaOfStudy,
            dateOfStudy,
            higherEducationBodyID
        ))

        storage.saveTokens(dto.accessToken!!, dto.refreshToken!!)

        return UserRegister(
            success = dto.message,
            error = dto.message
        )
    }
}
