package hr.wortex.otpstudent.data.remote.api

import hr.wortex.otpstudent.data.remote.dto.JwtDto
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface IOtpApiService {

    @GET("api/getJWT")
    suspend fun getJwt(): JwtDto

    @GET("api/current-user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserDto

    @POST("api/login")
    suspend fun login(
        @Body req: LoginDto
    ): LoginResponseDto
}