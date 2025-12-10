package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.StudentJobApplicationDto
import hr.wortex.otpstudent.data.remote.dto.StudentJobDetailDto
import hr.wortex.otpstudent.data.remote.dto.StudentJobDto

interface IStudentJobRemoteDataSource {
    suspend fun getStudentJobs(): List<StudentJobDto>
    suspend fun getStudentJobDetails(id: Int): StudentJobDetailDto
    suspend fun applyToJob(jobId: Int): Boolean
    suspend fun getApplicationsForCurrentUser(): List<StudentJobApplicationDto>
}