package hr.wortex.otpstudent.domain.repository.interfaces

interface ITokenRepository {
    suspend fun getAccessTokenOrNull(): String?
    suspend fun refreshToken(): String?
}