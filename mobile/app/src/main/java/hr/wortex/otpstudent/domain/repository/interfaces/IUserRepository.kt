package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.data.remote.dto.UpdateUserDto
import hr.wortex.otpstudent.domain.model.User

interface IUserRepository {
    suspend fun getCurrentUser(): User
    suspend fun updateUser(token: String, update: UpdateUserDto): Boolean
}