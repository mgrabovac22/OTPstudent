package hr.wortex.otpstudent.domain.repository

import hr.wortex.otpstudent.data.remote.datasource.InfoContentRemoteDataSource
import hr.wortex.otpstudent.domain.model.InfoContent
import hr.wortex.otpstudent.domain.repository.interfaces.IInfoContentRepository

class InfoContentRepository(private val remoteDataSource: InfoContentRemoteDataSource) :
    IInfoContentRepository {
    override suspend fun getInfoContent(): List<InfoContent> {
        val dtoList = remoteDataSource.getInfoContent()

        return dtoList.map { dto ->
            InfoContent(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                experiencePoints = dto.experiencePoints
            )
        }
    }

    override suspend fun getInfoContentById(id: Int): InfoContent {
        val dto = remoteDataSource.getInfoContentById(id)

        return InfoContent(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            experiencePoints = dto.experiencePoints
        )
    }

    override suspend fun markContentAsRead(userId: Int, contentId: Int) {
        remoteDataSource.markContentAsRead(userId, contentId)
    }
}