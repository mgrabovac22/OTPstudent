package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.InternshipApplication
import hr.wortex.otpstudent.domain.model.InternshipJob

interface IInternshipRepository {
    suspend fun applyToInternship(application: InternshipApplication): Boolean
    suspend fun getInternshipJobs(): List<InternshipJob>
}
