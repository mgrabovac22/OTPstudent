package hr.wortex.otpstudent.data.remote.datasource.interfaces

import hr.wortex.otpstudent.data.remote.dto.InfoDTO

interface IInfoContentRemoteDataSource {
    suspend fun getInfoContent(): List<InfoDTO>
}