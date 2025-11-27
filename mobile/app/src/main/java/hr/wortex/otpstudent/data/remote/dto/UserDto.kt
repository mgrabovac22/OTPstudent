package hr.wortex.otpstudent.data.remote.dto

data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val yearOfStudy: Int?,
    val areaOfStudy: String?,
    val imagePath: String?,
    val cvPath: String?,
    val dateOfBirth: String?
)