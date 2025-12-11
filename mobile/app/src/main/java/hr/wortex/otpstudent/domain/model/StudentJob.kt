package hr.wortex.otpstudent.domain.model

data class StudentJob(
    val id: Int,
    val name: String,
    val startDate: String,
    val location: String,
    val city: String,
    val isApplied: Boolean = false
)
