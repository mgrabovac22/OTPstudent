package hr.wortex.otpstudent.domain.model

data class InternshipApplication(
    val id: Int,
    val studentExpectations: String,
    val studentAdress: String,
    val contactNumber: String,
    val dateOfApplication: String,
    val duration: Int,
    val expectedStartDate: String,
    val expectedEndDate: String,
    val expectedJobs: List<Int>
)