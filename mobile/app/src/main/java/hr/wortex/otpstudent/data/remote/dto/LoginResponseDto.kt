package hr.wortex.otpstudent.data.remote.dto

data class LoginResponseDto(
    val success: String?,
    val error: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val email: String?
)
