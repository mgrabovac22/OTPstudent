package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.model.InfoContent
import hr.wortex.otpstudent.domain.repository.interfaces.IInfoContentRepository

class GetInfoContentDetail(
    private val repository: IInfoContentRepository
) {
    suspend operator fun invoke(id: Int): InfoContent {
        return repository.getInfoContentById(id)
    }
}