package hr.wortex.otpstudent.data.remote.dto

data class LoginDto(
    val email: String,
    val hashPassword: String
)
