package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService

class UsersRemoteDataSource(
    private val api: IOtpApiService
) : IUsersRemoteDataSource {
}