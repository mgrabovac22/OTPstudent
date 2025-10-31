package hr.wortex.otpstudent.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.usecase.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Loading : LoginUiState()
    object Idle : LoginUiState()
    data class Success(val userLogin: UserLogin) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val loginUseCase: Login) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val result = loginUseCase(email, password)
                _uiState.value = LoginUiState.Success(result)
            } catch (e: retrofit2.HttpException) {
                val message = "Server error: ${e.code()}"
                _uiState.value = LoginUiState.Error(message)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

class LoginViewModelFactory(private val loginUseCase: Login) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
