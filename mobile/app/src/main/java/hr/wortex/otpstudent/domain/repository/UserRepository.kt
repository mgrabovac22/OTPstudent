package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IJwtRepository
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository

class UserRepository(private val remoteDataSource: UsersRemoteDataSource,  private val jwtRepository: IJwtRepository) :
    IUserRepository {
    override suspend fun getCurrentUser(): User {
        val token = jwtRepository.getJwtToken().token
        val dto = remoteDataSource.getCurrentUser(token)
        return User(
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            yearOfStudy = dto.yearOfStudy,
            areaOfStudy = dto.areaOfStudy,
            imagePath = dto.imagePath,
            cvPath = dto.cvPath,
            dateOfBirth = dto.dateOfBirth
        )
    }
}