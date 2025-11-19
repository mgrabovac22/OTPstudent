package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.InternshipJob
import hr.wortex.otpstudent.domain.repository.interfaces.IInternshipRepository

class GetJobs(private val repository: IInternshipRepository) {
    suspend operator fun invoke(): List<InternshipJob> {
        return repository.getInternshipJobs()
    }
}