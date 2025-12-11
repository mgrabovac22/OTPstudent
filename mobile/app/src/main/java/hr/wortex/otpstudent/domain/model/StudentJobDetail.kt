package hr.wortex.otpstudent.domain.model

data class StudentJobDetail(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val locationId: Int,
    val location: String,
    val city: String
)
