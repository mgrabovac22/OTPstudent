package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.domain.repository.interfaces.ITokenRepository

class TokenRepository(
    private val api: IOtpApiService,
    private val storage: TokenStorage
) : ITokenRepository {

    override suspend fun getAccessTokenOrNull() = storage.getAccessToken()

    override suspend fun refreshToken(): String? {
        val refresh = storage.getRefreshToken() ?: return null

        val result = api.refresh(mapOf("refreshToken" to refresh))

        storage.saveTokens(result.accessToken, refresh)

        return result.accessToken
    }
}
