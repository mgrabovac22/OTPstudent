package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.InternshipApplication
import hr.wortex.otpstudent.domain.repository.interfaces.IInternshipRepository

class ApplyToInternship(private val repository: IInternshipRepository) {
    suspend operator fun invoke(application: InternshipApplication): Boolean {
        return repository.applyToInternship(application)
    }
}