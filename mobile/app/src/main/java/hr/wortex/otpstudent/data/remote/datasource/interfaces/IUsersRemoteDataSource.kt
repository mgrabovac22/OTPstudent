package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.UpdateResponse
import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import okhttp3.MultipartBody

interface IUsersRemoteDataSource {
    suspend fun getCurrentUser(): UserDto
    suspend fun uploadCv(filePart: MultipartBody.Part): Map<String, String>
    suspend fun updateUser(token: String, update: UpdateUserDto): UpdateResponse
}