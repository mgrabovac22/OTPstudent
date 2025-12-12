package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInternshipRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.InternshipApplicationDto
import hr.wortex.otpstudent.data.remote.dto.InternshipJobDto

class InternshipRemoteDataSource(
    private val api: IOtpApiService
) : IInternshipRemoteDataSource {
    override suspend fun applyToInternship(application: InternshipApplicationDto): Boolean {
        val response = api.applyToInternship(application)
        return response.isSuccessful
    }

    override suspend fun getInternshipJobs(): List<InternshipJobDto> {
        return api.getInternshipJobs()
    }
}