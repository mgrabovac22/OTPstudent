package hr.wortex.otpstudent.data.remote.dto

data class RegisterResponseDto(
    val message: String,
    val accessToken: String?,
    val refreshToken: String?
)
