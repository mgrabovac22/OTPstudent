package hr.wortex.otpstudent.domain.usecase

import hr.wortex.otpstudent.domain.repository.interfaces.IInfoContentRepository

class MarkInfoContentRead (
    private val infoContentRepository: IInfoContentRepository
) {
    suspend operator fun invoke(userId: Int, contentId: Int) {
        infoContentRepository.markContentAsRead(userId, contentId)
    }
}