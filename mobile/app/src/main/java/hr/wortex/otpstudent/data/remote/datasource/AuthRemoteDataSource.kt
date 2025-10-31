package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IAuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto

class AuthRemoteDataSource(
    private val api: IOtpApiService
) : IAuthRemoteDataSource {

    override suspend fun login(req: LoginDto): LoginResponseDto {
        return api.login(isMobile = "true", req)
    }
}