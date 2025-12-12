package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.domain.repository.interfaces.ITokenRepository
import retrofit2.HttpException
import java.io.IOException

class TokenRepository(
    private val api: IOtpApiService,
    private val storage: TokenStorage
) : ITokenRepository {

    override suspend fun getAccessTokenOrNull() = storage.getAccessToken()

    override suspend fun refreshToken(): String? {
        val refresh = storage.getRefreshToken() ?: return null

        return try {
            val result = api.refresh(mapOf("refreshToken" to refresh))

            storage.saveTokens(result.accessToken, result.refreshToken)

            result.accessToken
        } catch (e: HttpException) {
            println("Refresh token failed (HTTP ${e.code()}): ${e.message()}")
            storage.clear()
            null
        } catch (e: IOException) {
            println("Refresh token failed (Network Error): ${e.message}")
            null
        }
    }
}