package hr.wortex.otpstudent.ui.unlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UnlockUiState {
    object Idle : UnlockUiState()
    object Loading : UnlockUiState()
    data class Success(val user: User) : UnlockUiState()
    data class Error(val message: String) : UnlockUiState()
}

class UnlockViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UnlockUiState>(UnlockUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun verifySession() {
        viewModelScope.launch {
            _uiState.value = UnlockUiState.Loading
            try {
                val user = userRepository.getCurrentUser()
                _uiState.value = UnlockUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UnlockUiState.Error(e.message ?: "Nepoznata greška")
            }
        }
    }
}

class UnlockViewModelFactory(private val userRepository: IUserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnlockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnlockViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
