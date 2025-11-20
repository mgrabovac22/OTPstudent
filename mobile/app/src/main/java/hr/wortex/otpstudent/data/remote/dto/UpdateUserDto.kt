package hr.wortex.otpstudent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserDto(
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("yearOfStudy") val yearOfStudy: Int?,
    @SerializedName("areaOfStudy") val areaOfStudy: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("imagePath") val imagePath: String?,
    @SerializedName("cvPath") val cvPath: String?,
    @SerializedName("password") val password: String?
)