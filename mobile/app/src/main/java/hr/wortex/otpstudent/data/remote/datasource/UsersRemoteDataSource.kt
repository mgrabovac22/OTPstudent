package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IUsersRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.UpdateResponse
import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.data.remote.dto.UserDto

class UsersRemoteDataSource(
    private val api: IOtpApiService
) : IUsersRemoteDataSource {
    override suspend fun getCurrentUser(): UserDto {
        return api.getCurrentUser()
    }

    override suspend fun updateUser(token: String, update: UpdateUserDto): UpdateResponse {
        return api.updateUser(update, "Bearer $token")
    }
}