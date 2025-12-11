package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.StudentJob
import hr.wortex.otpstudent.domain.repository.interfaces.IStudentJobRepository

class GetStudentJobs(
    private val repository: IStudentJobRepository
) {
    suspend operator fun invoke(): List<StudentJob> = repository.getStudentJobs()
}
