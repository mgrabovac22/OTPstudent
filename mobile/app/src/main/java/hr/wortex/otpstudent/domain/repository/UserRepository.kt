package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource

class UserRepository(private val remoteDataSource: UsersRemoteDataSource) :IUserRepository {
}