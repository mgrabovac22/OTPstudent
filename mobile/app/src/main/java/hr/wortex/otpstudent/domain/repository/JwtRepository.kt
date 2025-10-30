package hr.wortex.otpstudent.domain.repository

import android.util.Log
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.domain.model.JwtToken
import hr.wortex.otpstudent.domain.repository.interfaces.IJwtRepository

class JwtRepository(
    private val remoteDataSource: AuthRemoteDataSource
) : IJwtRepository {

    private var cachedToken: JwtToken? = null

    override suspend fun getJwtToken(): JwtToken {
        if (cachedToken == null) {
            val dto = remoteDataSource.getJwt()
            cachedToken = JwtToken(dto.token)
        }
        Log.d("AuthRemoteDataSource", "JWT response: $cachedToken")
        return cachedToken!!
    }
}
