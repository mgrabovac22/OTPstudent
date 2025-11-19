package hr.wortex.otpstudent.di

import hr.wortex.otpstudent.App
import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.AuthInterceptor
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.repository.AuthRepository
import hr.wortex.otpstudent.domain.repository.TokenRepository
import hr.wortex.otpstudent.domain.repository.UserRepository
import hr.wortex.otpstudent.domain.usecase.Login
import hr.wortex.otpstudent.ui.unlock.UnlockViewModelFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    private val storage by lazy { TokenStorage(App.instance) }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:8000/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: IOtpApiService by lazy { retrofit.create(IOtpApiService::class.java) }

    private val tokenRepo: TokenRepository by lazy { TokenRepository(apiService, storage) }

    private val authInterceptor: AuthInterceptor by lazy { AuthInterceptor(tokenRepo) }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    private val retrofitWithInterceptor: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiServiceWithInterceptor: IOtpApiService by lazy { retrofitWithInterceptor.create(IOtpApiService::class.java) }

    val authRepository by lazy { AuthRepository(AuthRemoteDataSource(apiServiceWithInterceptor), storage) }
    val userRepository by lazy { UserRepository(UsersRemoteDataSource(apiServiceWithInterceptor)) }
    val login by lazy { Login(authRepository) }

    val unlockViewModelFactory by lazy { UnlockViewModelFactory(userRepository) }
}
