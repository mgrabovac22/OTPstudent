package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.JwtToken

interface IJwtRepository {
    suspend fun getJwtToken(): JwtToken
}
