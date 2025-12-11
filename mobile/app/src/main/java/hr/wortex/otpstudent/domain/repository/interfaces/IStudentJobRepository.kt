package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.StudentJob
import hr.wortex.otpstudent.domain.model.StudentJobDetail

interface IStudentJobRepository {
    suspend fun getStudentJobs(): List<StudentJob>
    suspend fun getStudentJobDetails(id: Int): StudentJobDetail
    suspend fun applyToJob(jobId: Int): Boolean
    suspend fun unapplyFromJob(jobId: Int): Boolean
    suspend fun hasAppliedToJob(jobId: Int): Boolean
}