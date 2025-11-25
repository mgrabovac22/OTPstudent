package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.InstitutionRemoteDataSource
import hr.wortex.otpstudent.domain.model.Institution
import hr.wortex.otpstudent.domain.repository.interfaces.IInstitutionRepository

class InstitutionRepository(
    private val remote: InstitutionRemoteDataSource
): IInstitutionRepository {
    override suspend fun getAllInstitutions(): List<Institution> {
        val dtos = remote.getAllInstitutions()

        return dtos.map { dto ->
            Institution(
                dto.id,
                dto.name
            )
        }
    }
}