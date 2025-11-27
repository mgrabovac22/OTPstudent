package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.UserRegister
import hr.wortex.otpstudent.domain.repository.interfaces.IAuthRepository

class Register(private val authRepository: IAuthRepository) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        hashPassword: String,
        yearOfStudy: Number,
        areaOfStudy: String,
        dateOfStudy: String,
        higherEducationBodyID: Int
    ): UserRegister {
        return authRepository.register(
            firstName,
            lastName,
            email,
            hashPassword,
            yearOfStudy,
            areaOfStudy,
            dateOfStudy,
            higherEducationBodyID
        )
    }
}