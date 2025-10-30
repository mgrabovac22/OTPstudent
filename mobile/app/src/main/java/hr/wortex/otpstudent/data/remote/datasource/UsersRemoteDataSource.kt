package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IUsersRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.UserDto

class UsersRemoteDataSource(
    private val api: IOtpApiService
) : IUsersRemoteDataSource {
    override suspend fun getCurrentUser(token: String): UserDto {
        return api.getCurrentUser("Bearer $token")
    }
}