package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.UserDto

interface IUsersRemoteDataSource {
    suspend fun getCurrentUser(token: String): UserDto
}