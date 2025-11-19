package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.InternshipApplicationDto
import hr.wortex.otpstudent.data.remote.dto.InternshipJobDto

interface IInternshipRemoteDataSource {
    suspend fun applyToInternship(application: InternshipApplicationDto): Boolean
    suspend fun getInternshipJobs(): List<InternshipJobDto>
}