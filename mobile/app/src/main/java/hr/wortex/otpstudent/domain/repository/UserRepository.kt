package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository
import okhttp3.MultipartBody

class UserRepository(private val remoteDataSource: UsersRemoteDataSource) :
    IUserRepository {
    override suspend fun getCurrentUser(): User {
        val dto = remoteDataSource.getCurrentUser()
        return User(
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            yearOfStudy = dto.yearOfStudy,
            areaOfStudy = dto.areaOfStudy,
            imagePath = dto.imagePath,
            cvPath = dto.cvPath,
            dateOfBirth = dto.dateOfBirth,
            image = dto.image
        )
    }

    override suspend fun uploadCv(filePart: MultipartBody.Part): Boolean {
        val response = remoteDataSource.uploadCv(filePart)
        return response["success"] != null
    }
}