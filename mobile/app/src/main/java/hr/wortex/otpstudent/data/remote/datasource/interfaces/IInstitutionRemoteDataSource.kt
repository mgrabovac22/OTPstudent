package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.InstitutionDto

interface IInstitutionRemoteDataSource {
    suspend fun getAllInstitutions(): List<InstitutionDto>
}