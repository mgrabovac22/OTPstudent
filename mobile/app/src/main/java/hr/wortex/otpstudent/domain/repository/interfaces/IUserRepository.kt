package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.User
import okhttp3.MultipartBody

interface IUserRepository {
    suspend fun getCurrentUser(): User
    suspend fun uploadCv(filePart: MultipartBody.Part): Boolean
    suspend fun updateUser(user: User): Boolean
    suspend fun uploadImage(filePart: MultipartBody.Part): Boolean
}