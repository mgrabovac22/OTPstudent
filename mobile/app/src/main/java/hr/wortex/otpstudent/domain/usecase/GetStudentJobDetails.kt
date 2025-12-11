package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.StudentJobDetail
import hr.wortex.otpstudent.domain.repository.interfaces.IStudentJobRepository

class GetStudentJobDetails(
    private val repository: IStudentJobRepository
) {
    suspend operator fun invoke(id: Int): StudentJobDetail =
        repository.getStudentJobDetails(id)
}
