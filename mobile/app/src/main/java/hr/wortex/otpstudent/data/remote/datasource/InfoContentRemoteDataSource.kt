package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.dto.InfoDTO
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInfoContentRemoteDataSource
import hr.wortex.otpstudent.data.remote.dto.MarkReadDTO

class InfoContentRemoteDataSource (
    private val api: IOtpApiService
) : IInfoContentRemoteDataSource {
    override suspend fun getInfoContent(): List<InfoDTO> {
        return api.getInfoContent()
    }

    override suspend fun getInfoContentById(id: Int): InfoDTO {
        return api.getInfoContentById(id)
    }

    override suspend fun markContentAsRead(userId: Int, contentId: Int) {
        val request = MarkReadDTO(userId = userId, contentId = contentId)
        api.markContentAsRead(request)
    }
}