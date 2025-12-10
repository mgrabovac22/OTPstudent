package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IStudentJobRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.StudentJobApplicationDto
import hr.wortex.otpstudent.data.remote.dto.StudentJobApplicationRequestDto
import hr.wortex.otpstudent.data.remote.dto.StudentJobDetailDto
import hr.wortex.otpstudent.data.remote.dto.StudentJobDto

class StudentJobRemoteDataSource(
    private val api: IOtpApiService
) : IStudentJobRemoteDataSource {

    override suspend fun getStudentJobs(): List<StudentJobDto> {
        return api.getStudentJobs()
    }

    override suspend fun getStudentJobDetails(id: Int): StudentJobDetailDto {
        return api.getStudentJobDetails(id)
    }

    override suspend fun applyToJob(jobId: Int): Boolean {
        val request = StudentJobApplicationRequestDto(studentJobId = jobId)
        val response = api.applyToStudentJob(request)

        return response.isSuccessful || response.code() == 409
    }

    override suspend fun getApplicationsForCurrentUser(): List<StudentJobApplicationDto> {
        return api.getStudentJobApplications()
    }
}