package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.Institution

interface IInstitutionRepository {
    suspend fun getAllInstitutions(): List<Institution>
}