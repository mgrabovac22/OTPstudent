package hr.wortex.otpstudent.ui.login

import hr.wortex.otpstudent.domain.usecase.Login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState{
    object Loading: LoginUiState()
    object Idle: LoginUiState()
    data class Success(val userLogin: UserLogin): LoginUiState()
    data class Error(val message: String): LoginUiState()
}

class LoginViewModel(private val loginUseCase: Login) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginUser(email: String, hashPassword: String){
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                val result = loginUseCase(email, hashPassword)
                _uiState.value = LoginUiState.Success(result)
            } catch (e: Exception){
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}