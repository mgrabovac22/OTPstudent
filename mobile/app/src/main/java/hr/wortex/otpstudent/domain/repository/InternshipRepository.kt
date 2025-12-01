package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInternshipRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.InternshipApplicationDto
import hr.wortex.otpstudent.domain.model.InternshipApplication
import hr.wortex.otpstudent.domain.model.InternshipJob
import hr.wortex.otpstudent.domain.repository.interfaces.IInternshipRepository

class InternshipRepository(
    private val remoteDataSource: IInternshipRemoteDataSource
) : IInternshipRepository {
    override suspend fun applyToInternship(application: InternshipApplication): Boolean {
        // Map domain model to DTO
        val dto = InternshipApplicationDto(
            studentExpectations = application.studentExpectations,
            studentAdress = application.studentAdress,
            contactNumber = application.contactNumber,
            duration = application.duration,
            expectedStartDate = application.expectedStartDate,
            expectedEndDate = application.expectedEndDate,
            expectedJobs = application.expectedJobs
        )
        return remoteDataSource.applyToInternship(dto)
    }

    override suspend fun getInternshipJobs(): List<InternshipJob> {
        return remoteDataSource.getInternshipJobs().map {
            InternshipJob(
                id = it.id,
                name = it.name
            )
        }
    }
}
