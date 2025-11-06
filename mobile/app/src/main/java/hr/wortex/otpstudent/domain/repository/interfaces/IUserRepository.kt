package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.User

interface IUserRepository {
    suspend fun getCurrentUser(): User
}