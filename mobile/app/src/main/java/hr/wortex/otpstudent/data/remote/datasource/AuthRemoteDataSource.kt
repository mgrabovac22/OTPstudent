package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IAuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.RegisterDto
import hr.wortex.otpstudent.data.remote.dto.RegisterResponseDto

class AuthRemoteDataSource(
    private val api: IOtpApiService
) : IAuthRemoteDataSource {

    override suspend fun login(req: LoginDto): LoginResponseDto {
        return api.login(isMobile = "true", req)
    }

    override suspend fun register(req: RegisterDto): RegisterResponseDto {
        return api.register(isMobile = "true", req)
    }
}