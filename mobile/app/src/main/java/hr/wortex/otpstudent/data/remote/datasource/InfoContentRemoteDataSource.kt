package hr.wortex.otpstudent.data.remote.datasource

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.dto.InfoDTO
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInfoContentRemoteDataSource

class InfoContentRemoteDataSource (
    private val api: IOtpApiService
) : IInfoContentRemoteDataSource {
    override suspend fun getInfoContent(): List<InfoDTO> {
        return api.getInfoContent()
    }
}