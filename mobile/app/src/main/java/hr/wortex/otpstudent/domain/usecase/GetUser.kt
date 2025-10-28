package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.IUserRepository

class GetUser(
    private val repository: IUserRepository
) {
}