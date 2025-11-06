package hr.wortex.otpstudent.data.remote

import hr.wortex.otpstudent.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenRepo: TokenRepository
) : Interceptor {

    private val noAuthPaths = listOf("/login", "/register", "/refresh")

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (noAuthPaths.any { request.url.encodedPath.contains(it) }) {
            return chain.proceed(request)
        }

        val accessToken = runBlocking { tokenRepo.getAccessTokenOrNull() }

        if (!accessToken.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        var response = chain.proceed(request)

        if (response.code == 401) {
            response.close()

            val newToken = runBlocking { tokenRepo.refreshToken() }

            if (!newToken.isNullOrBlank()) {
                val retryRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                response = chain.proceed(retryRequest)
            }
        }

        return response
    }
}
