package hr.wortex.otpstudent.di

import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository
import hr.wortex.otpstudent.domain.repository.JwtRepository
import hr.wortex.otpstudent.domain.repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy {
        retrofit.create(IOtpApiService::class.java)
    }

    private val userRemoteDataSource by lazy {
        UsersRemoteDataSource(apiService)
    }

    private val authRemoteDataSource by lazy {
        AuthRemoteDataSource(apiService)
    }

    private val jwtRepository by lazy {
        JwtRepository(authRemoteDataSource)
    }

    val userRepository: IUserRepository by lazy {
        UserRepository(userRemoteDataSource, jwtRepository)
    }
}