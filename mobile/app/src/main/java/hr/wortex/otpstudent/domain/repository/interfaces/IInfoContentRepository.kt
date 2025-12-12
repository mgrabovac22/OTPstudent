package hr.wortex.otpstudent.domain.repository.interfaces

import hr.wortex.otpstudent.domain.model.InfoContent

interface IInfoContentRepository {
    suspend fun getInfoContent(): List<InfoContent>
    suspend fun getInfoContentById(id: Int): InfoContent
    suspend fun markContentAsRead(userId: Int, contentId: Int)
}