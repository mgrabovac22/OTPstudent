package hr.wortex.otpstudent.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserProfile
import hr.wortex.otpstudent.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateProfile(user: UserProfile) {
        //TODO: implementirati otvaranje ekrana za edit
    }

    fun uploadCV(inputStream: InputStream) {
        //TODO: Implementirati spremanje CV-a na server
    }
}