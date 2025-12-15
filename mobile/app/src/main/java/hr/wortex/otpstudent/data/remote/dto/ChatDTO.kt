package hr.wortex.otpstudent.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("mode") val mode: String = "general",
    @SerializedName("history") val history: List<Map<String, String>> = emptyList(),
    @SerializedName("email") val email: String? = null
)

data class ChatResponseDto(
    @SerializedName("answer") val answer: String,
    @SerializedName("mode") val mode: String
)