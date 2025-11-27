package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.InfoDTO

interface IInfoContentRemoteDataSource {
    suspend fun getInfoContent(): List<InfoDTO>
    suspend fun getInfoContentById(id: Int) : InfoDTO
    suspend fun markContentAsRead(userId: Int, contentId: Int)
}