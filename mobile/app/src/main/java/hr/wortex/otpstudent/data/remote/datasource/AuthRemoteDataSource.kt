package hr.wortex.otpstudent.data.remote.datasource

import android.util.Log
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IAuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.JwtDto

class AuthRemoteDataSource(
    private val api: IOtpApiService
) : IAuthRemoteDataSource {

    override suspend fun getJwt(): JwtDto {
        val response = api.getJwt()
        Log.d("AuthRemoteDataSource", "JWT response: $response")
        return response
    }
}