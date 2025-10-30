package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.JwtDto

interface IAuthRemoteDataSource {
    suspend fun getJwt(): JwtDto
}