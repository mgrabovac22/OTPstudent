package hr.wortex.otpstudent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StudentJobApplicationDto(
    @SerializedName("Student_Job_id")
    val studentJobId: Int,
    val applicationDate: String,
    val name: String,
    val description: String
)
