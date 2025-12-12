package hr.wortex.otpstudent.data.remote.dto

data class StudentJobDetailDto(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val locationId: Int,
    val location: String,
    val city: String
)
