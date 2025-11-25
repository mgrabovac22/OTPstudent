package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInstitutionRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.InstitutionDto

class InstitutionRemoteDataSource(
    private val api: IOtpApiService
): IInstitutionRemoteDataSource {
    override suspend fun getAllInstitutions(): List<InstitutionDto> {
        return api.getInstitutions()
    }
}