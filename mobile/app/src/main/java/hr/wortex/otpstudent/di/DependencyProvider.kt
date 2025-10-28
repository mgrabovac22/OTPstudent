package hr.wortex.otpstudent.di

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.repository.IUserRepository
import hr.wortex.otpstudent.domain.repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://localhost:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy {
        retrofit.create(IOtpApiService::class.java)
    }

    private val remoteDataSource by lazy {
        UsersRemoteDataSource(apiService)
    }

    val userRepository: IUserRepository by lazy {
        UserRepository(remoteDataSource)
    }
}