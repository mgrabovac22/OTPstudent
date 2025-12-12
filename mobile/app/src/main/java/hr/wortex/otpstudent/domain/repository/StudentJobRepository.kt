package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.interfaces.IStudentJobRemoteDataSource
import hr.wortex.otpstudent.domain.model.StudentJob
import hr.wortex.otpstudent.domain.model.StudentJobDetail
import hr.wortex.otpstudent.domain.repository.interfaces.IStudentJobRepository

class StudentJobRepository(
    private val remoteDataSource: IStudentJobRemoteDataSource
) : IStudentJobRepository {

    override suspend fun getStudentJobs(): List<StudentJob> {
        val jobs = remoteDataSource.getStudentJobs()

        val applications = remoteDataSource.getApplicationsForCurrentUser()
        val appliedIds = applications.map { it.studentJobId }.toSet()

        return jobs.map { dto ->
            StudentJob(
                id = dto.id,
                name = dto.name,
                startDate = dto.startDate,
                location = dto.location,
                city = dto.city,
                isApplied = appliedIds.contains(dto.id)
            )
        }
    }

    override suspend fun getStudentJobDetails(id: Int): StudentJobDetail {
        val dto = remoteDataSource.getStudentJobDetails(id)
        return StudentJobDetail(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            locationId = dto.locationId,
            location = dto.location,
            city = dto.city
        )
    }

    override suspend fun applyToJob(jobId: Int): Boolean {
        return remoteDataSource.applyToJob(jobId)
    }

    override suspend fun unapplyFromJob(jobId: Int): Boolean {
        return remoteDataSource.unapplyFromJob(jobId)
    }

    override suspend fun hasAppliedToJob(jobId: Int): Boolean {
        val applications = remoteDataSource.getApplicationsForCurrentUser()
        return applications.any { it.studentJobId == jobId }
    }
}