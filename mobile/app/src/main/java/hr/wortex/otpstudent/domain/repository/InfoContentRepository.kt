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
                name = dto.name,
                description = dto.description,
                experiencePoints = dto.experiencePoints
            )
        }
    }
}