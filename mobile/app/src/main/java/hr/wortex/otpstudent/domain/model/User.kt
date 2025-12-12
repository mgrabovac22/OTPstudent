package hr.wortex.otpstudent.domain.model

data class User(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val yearOfStudy: Int?,
    val areaOfStudy: String?,
    val imagePath: String?,
    val cvPath: String?,
    val dateOfBirth: String?,
    val image: String?
)