package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.interfaces.IInfoContentRepository
import hr.wortex.otpstudent.domain.model.InfoContent

class GetInfoContent (
    private val infoContentRepository: IInfoContentRepository
) {
    suspend operator fun invoke(): List<InfoContent> {
        return infoContentRepository.getInfoContent()
    }
}