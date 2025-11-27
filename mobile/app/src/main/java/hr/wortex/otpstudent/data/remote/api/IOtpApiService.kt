package hr.wortex.otpstudent.data.remote.api

import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.RefreshDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import hr.wortex.otpstudent.data.remote.dto.InfoDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface IOtpApiService {

    @GET("api/current-user")
    suspend fun getCurrentUser(
    ): UserDto

    @POST("api/login")
    suspend fun login(
        @Header("x-mobile") isMobile: String = "true",
        @Body req: LoginDto
    ): LoginResponseDto

    @POST("api/refresh")
    suspend fun refresh(@Body body: Map<String, String>): RefreshDto

    @GET("api/info-content")
    suspend fun getInfoContent(
    ): List<InfoDTO>

    @GET("api/info-content/{id}")
    suspend fun getInfoContentById(@Path("id") id: Int): InfoDTO
}