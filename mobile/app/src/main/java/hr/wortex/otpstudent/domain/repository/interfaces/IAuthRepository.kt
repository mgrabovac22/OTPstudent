package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.model.UserRegister

interface IAuthRepository {
    suspend fun login(email: String, hashPassword: String): UserLogin
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        hashPassword: String,
        yearOfStudy: Number,
        areaOfStudy: String,
        dateOfStudy: String,
        higherEducationBodyID: Int
    ): UserRegister
}