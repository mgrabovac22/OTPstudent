package hr.wortex.otpstudent.data.remote.dto

data class UpdateUserDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val yearOfStudy: Int? = null,
    val areaOfStudy: String? = null,
    val password: String? = null,
    val imagePath: String? = null,
    val cvPath: String? = null,
    val dateOfBirth: String? = null
)

data class UpdateResponse(
    val success: String?,
    val error: String?
)
