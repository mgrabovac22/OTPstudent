package hr.wortex.otpstudent.data.remote.api

import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.RefreshDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("api/upload-cv")
    suspend fun uploadCv(
        @Part cv: MultipartBody.Part
    ): Map<String, String>
}