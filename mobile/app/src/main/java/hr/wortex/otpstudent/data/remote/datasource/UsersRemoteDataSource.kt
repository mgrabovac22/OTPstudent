package hr.wortex.otpstudent.data.remote.datasource

import android.content.ContentValues.TAG
import android.util.Log
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IUsersRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.ChangePasswordDto
import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.data.remote.dto.UserDto
import okhttp3.MultipartBody

class UsersRemoteDataSource(
    private val api: IOtpApiService
) : IUsersRemoteDataSource {
    override suspend fun getCurrentUser(): UserDto {
        return api.getCurrentUser()
    }

    override suspend fun uploadCv(filePart: MultipartBody.Part): Map<String, String> {
        return api.uploadCv(filePart)
    }

    override suspend fun updateUser(body: UpdateUserDto): Map<String, String> {
        return api.updateUser(body)
    }

    override suspend fun uploadImage(filePart: MultipartBody.Part): Map<String, String> {
        return api.uploadImage(filePart)
    }

    override suspend fun changePassword(oldPassword: String, password: String): Map<String, String> {
        Log.d(TAG, "Attempting to change password for user.")
        return api.changePassword(ChangePasswordDto(oldPassword, password))
    }

}