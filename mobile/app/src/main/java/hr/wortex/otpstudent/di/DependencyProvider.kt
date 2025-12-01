package hr.wortex.otpstudent.di

import hr.wortex.otpstudent.App
import hr.wortex.otpstudent.data.local.TokenStorage
import hr.wortex.otpstudent.data.remote.AuthInterceptor
import hr.wortex.otpstudent.data.remote.api.IOtpApiService
import hr.wortex.otpstudent.data.remote.datasource.AuthRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.InstitutionRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.InfoContentRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.UsersRemoteDataSource
import hr.wortex.otpstudent.domain.repository.AuthRepository
import hr.wortex.otpstudent.domain.repository.InstitutionRepository
import hr.wortex.otpstudent.domain.repository.InfoContentRepository
import hr.wortex.otpstudent.domain.repository.TokenRepository
import hr.wortex.otpstudent.domain.repository.UserRepository
import hr.wortex.otpstudent.domain.usecase.GetInstitutions
import hr.wortex.otpstudent.domain.usecase.Login
import hr.wortex.otpstudent.domain.usecase.Register
import hr.wortex.otpstudent.data.remote.datasource.InternshipRemoteDataSource
import hr.wortex.otpstudent.data.remote.datasource.interfaces.IInternshipRemoteDataSource
import hr.wortex.otpstudent.domain.repository.InternshipRepository
import hr.wortex.otpstudent.domain.repository.interfaces.IInternshipRepository
import hr.wortex.otpstudent.domain.usecase.ApplyToInternship
import hr.wortex.otpstudent.domain.usecase.GetJobs
import hr.wortex.otpstudent.ui.internship.InternshipApplicationViewModelFactory
import hr.wortex.otpstudent.ui.unlock.UnlockViewModelFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import hr.wortex.otpstudent.domain.usecase.GetInfoContentDetail
import hr.wortex.otpstudent.domain.usecase.GetUser
import hr.wortex.otpstudent.domain.usecase.MarkInfoContentRead

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
    val register by lazy { Register(authRepository)}

    val institutionRepository by lazy { InstitutionRepository(InstitutionRemoteDataSource(apiServiceWithInterceptor)) }
    val getAllInstitutions by lazy { GetInstitutions(institutionRepository) }

    val infoContentRepository by lazy { InfoContentRepository(InfoContentRemoteDataSource(apiServiceWithInterceptor)) }

    private val internshipRemoteDataSource: IInternshipRemoteDataSource by lazy { InternshipRemoteDataSource(apiServiceWithInterceptor) }
    val internshipRepository: IInternshipRepository by lazy { InternshipRepository(internshipRemoteDataSource) }
    val getJobs by lazy { GetJobs(internshipRepository) }
    val applyToInternship by lazy { ApplyToInternship(internshipRepository) }

    val unlockViewModelFactory by lazy { UnlockViewModelFactory(userRepository) }

    val getInfoContentDetailUseCase by lazy { GetInfoContentDetail(infoContentRepository) }

    val markInfoContentReadUseCase by lazy { MarkInfoContentRead(infoContentRepository) }

    val getUserUseCase by lazy { GetUser(userRepository) }
    val internshipApplicationViewModelFactory by lazy {
        InternshipApplicationViewModelFactory(userRepository, internshipRepository, applyToInternship)
    }
}
