package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.interfaces.IStudentJobRepository

class ApplyToStudentJob(
    private val repository: IStudentJobRepository
) {
    suspend operator fun invoke(jobId: Int): Boolean =
        repository.applyToJob(jobId)
}

class UnapplyFromStudentJob(
    private val repository: IStudentJobRepository
) {
    suspend operator fun invoke(jobId: Int): Boolean =
        repository.unapplyFromJob(jobId)
}

class HasStudentAppliedToJob(
    private val repository: IStudentJobRepository
) {
    suspend operator fun invoke(jobId: Int): Boolean {
        return repository.hasAppliedToJob(jobId)
    }
}
