package hr.wortex.otpstudent.domain.model

data class UserProfile(
    val email: String,
    val firstName: String,
    val lastName: String,
    val yearOfStudy: Int?,
    val areaOfStudy: String?,
    val imagePath: String?,
    val cvPath: String?,
    val image: String?
)
