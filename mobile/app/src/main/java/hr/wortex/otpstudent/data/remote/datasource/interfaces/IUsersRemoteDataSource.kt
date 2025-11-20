package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.UpdateResponse
import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.data.remote.dto.UserDto

interface IUsersRemoteDataSource {
    suspend fun getCurrentUser(): UserDto
    suspend fun updateUser(token: String, update: UpdateUserDto): UpdateResponse
}