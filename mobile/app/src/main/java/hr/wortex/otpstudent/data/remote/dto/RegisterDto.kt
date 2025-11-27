package hr.wortex.otpstudent.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.sql.Date

data class RegisterDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val hashPassword: String,
    val yearOfStudy: Number,
    val areaOfStudy: String,
    val dateOfBirth: String,
    @SerializedName("Higher_Education_Body_id") val higherEducationBodyID: Int
)
