package hr.wortex.otpstudent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StudentJobApplicationRequestDto(
    @SerializedName("Student_Job_id")
    val studentJobId: Int
)
