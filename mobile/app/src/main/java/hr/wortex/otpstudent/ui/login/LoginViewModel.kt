package hr.wortex.otpstudent.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserLogin
import hr.wortex.otpstudent.domain.usecase.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Molimo unesite sve potrebne podatke."
                    401 -> "Neispravna e-mail adresa ili lozinka."
                    404 -> "Korisnik nije pronađen."
                    500 -> "Greška na poslužitelju, pokušajte kasnije."
                    else -> "Neočekivana greška (${e.code()})."
                }
                _uiState.value = LoginUiState.Error(message)
            } catch (e: IOException) {
                _uiState.value = LoginUiState.Error("Nema internetske veze. Provjerite mrežu.")
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Dogodila se greška: ${e.localizedMessage}")
            }
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Potrebno je unijeti Email adresu"
            !ValidationUtils.isEmailValid(email) -> "Neispravan format Email adrese"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Potrebno je unijeti lozinku"
            !ValidationUtils.isPasswordValid(password) -> "Lozinka mora imati barem 6 znakova"
            else -> null
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
