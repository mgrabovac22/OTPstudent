package hr.wortex.otpstudent.data.remote

import hr.wortex.otpstudent.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody

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

        val originalResponse = chain.proceed(request)

        if (originalResponse.code == 401) {

            originalResponse.body?.close()

            val newToken = runBlocking { tokenRepo.refreshToken() }

            if (!newToken.isNullOrBlank()) {
                val retryRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

                return chain.proceed(retryRequest)
            } else {
                val message = "{\"error\":\"Token refresh failed. Re-login required.\"}"
                val body = message.toResponseBody("application/json".toMediaTypeOrNull())

                return Response.Builder()
                    .request(request)
                    .protocol(originalResponse.protocol)
                    .code(401)
                    .message("Unauthorized (Refresh Failed)")
                    .body(body)
                    .build()
            }
        }

        return originalResponse
    }
}