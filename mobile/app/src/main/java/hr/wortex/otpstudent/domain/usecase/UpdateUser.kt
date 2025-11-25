package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository

class UpdateUser(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(user: User): Boolean {
        return repository.updateUser(user)
    }
}
