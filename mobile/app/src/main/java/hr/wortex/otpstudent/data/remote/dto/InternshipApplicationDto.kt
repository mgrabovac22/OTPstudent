package hr.wortex.otpstudent.data.remote.dto

data class InternshipApplicationDto(
    val studentExpectations: String,
    val studentAdress: String,
    val contactNumber: String,
    val duration: Int,
    val expectedStartDate: String,
    val expectedEndDate: String,
    val expectedJobs: List<Int>
)