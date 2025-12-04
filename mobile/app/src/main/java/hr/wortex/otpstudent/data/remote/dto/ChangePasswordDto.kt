package hr.wortex.otpstudent.data.remote.dto

data class ChangePasswordDto(
    val oldPassword: String,
    val password: String
)
