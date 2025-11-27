package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.Institution
import hr.wortex.otpstudent.domain.repository.interfaces.IInstitutionRepository

class GetInstitutions(private val institutionRepository: IInstitutionRepository) {
    suspend operator fun invoke(): List<Institution> {
        return institutionRepository.getAllInstitutions()
    }
}