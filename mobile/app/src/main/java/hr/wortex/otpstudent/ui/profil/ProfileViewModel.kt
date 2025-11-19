package hr.wortex.otpstudent.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserProfile
import hr.wortex.otpstudent.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val user = userRepository.getCurrentUser()

                val userProfile = UserProfile(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    yearOfStudy = user.yearOfStudy,
                    areaOfStudy = user.areaOfStudy,
                    imagePath = user.imagePath,
                    cvPath = user.cvPath
                )

                _uiState.value = ProfileUiState.Success(userProfile)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Greška prilikom učitavanja profila.")
            }
        }
    }

    fun updateProfile(user: UserProfile) {
        //TODO: implementirati otvaranje ekrana za edit
    }

    fun uploadCV(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                val bytes = inputStream.readBytes()

                val requestBody = bytes.toRequestBody(
                    "application/pdf".toMediaTypeOrNull()
                )

                val part = MultipartBody.Part.createFormData(
                    "cv",
                    "cv.pdf",
                    requestBody
                )

                val success = userRepository.uploadCv(part)

                if (success) {
                    loadUserProfile()
                } else {
                    _uiState.value = ProfileUiState.Error("Upload nije uspio")
                }

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Greška tijekom uploadanja: ${e.message}")
            }
        }
    }

    fun openEditScreen(){
        //TODO: Implement navigation event (možda kroz StateFlow<UiEvent>)
    }

    fun previousScreen(){
        //TODO: Implement navigation back event
    }
}