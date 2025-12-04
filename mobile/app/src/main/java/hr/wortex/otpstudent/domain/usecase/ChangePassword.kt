package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository

class ChangePassword(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(oldPassword: String, password: String): Boolean {
        return repository.changePassword(oldPassword, password)
    }
}
