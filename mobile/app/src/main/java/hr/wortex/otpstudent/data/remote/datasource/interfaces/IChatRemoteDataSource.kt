package hr.wortex.otpstudent.data.remote.datasource.interfaces

interface IChatRemoteDataSource {
    suspend fun sendMessage(message: String, history: List<Map<String, String>>, email: String?): String
}