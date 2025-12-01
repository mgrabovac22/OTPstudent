package hr.wortex.otpstudent.data.remote.api

import hr.wortex.otpstudent.data.remote.dto.InstitutionDto
import hr.wortex.otpstudent.data.remote.dto.InternshipApplicationDto
import hr.wortex.otpstudent.data.remote.dto.InternshipJobDto
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.RefreshDto
import hr.wortex.otpstudent.data.remote.dto.RegisterDto
import hr.wortex.otpstudent.data.remote.dto.RegisterResponseDto
import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import okhttp3.MultipartBody
import hr.wortex.otpstudent.data.remote.dto.InfoDTO
import hr.wortex.otpstudent.data.remote.dto.MarkReadDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @POST("api/register")
    suspend fun register(
        @Header("x-mobile") isMobile: String = "true",
        @Body req: RegisterDto
    ): RegisterResponseDto

    @POST("api/refresh")
    suspend fun refresh(@Body body: Map<String, String>): RefreshDto

    @Multipart
    @POST("api/upload-cv")
    suspend fun uploadCv(
        @Part cv: MultipartBody.Part
    ): Map<String, String>

    @PUT("api/update-user")
    suspend fun updateUser(
        @Body body: UpdateUserDto
    ): Map<String, String>

    @Multipart
    @POST("api/upload-image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Map<String, String>

    @GET("api/institutions")
    suspend fun getInstitutions(
    ): List<InstitutionDto>

    @GET("api/info-content")
    suspend fun getInfoContent(
    ): List<InfoDTO>

    @GET("api/info-content/{id}")
    suspend fun getInfoContentById(@Path("id") id: Int): InfoDTO

    @POST("api/info-content/read")
    suspend fun markContentAsRead(@Body request: MarkReadDTO)

    @GET("api/internship/jobs")
    suspend fun getInternshipJobs(): List<InternshipJobDto>

    @POST("api/internship/apply")
    suspend fun applyToInternship(
        @Body application: InternshipApplicationDto
    ): retrofit2.Response<Unit>
}