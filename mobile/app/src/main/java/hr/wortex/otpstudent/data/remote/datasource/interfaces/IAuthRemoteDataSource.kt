package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto

interface IAuthRemoteDataSource {
    suspend fun login(req: LoginDto): LoginResponseDto
}