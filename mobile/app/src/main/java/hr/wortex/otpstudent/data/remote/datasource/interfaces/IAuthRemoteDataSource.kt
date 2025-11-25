package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.LoginDto
import hr.wortex.otpstudent.data.remote.dto.LoginResponseDto
import hr.wortex.otpstudent.data.remote.dto.RegisterDto
import hr.wortex.otpstudent.data.remote.dto.RegisterResponseDto

interface IAuthRemoteDataSource {
    suspend fun login(req: LoginDto): LoginResponseDto
    suspend fun register(req: RegisterDto): RegisterResponseDto
}