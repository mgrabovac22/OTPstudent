package hr.wortex.otpstudent.ui.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.UserRegister
import hr.wortex.otpstudent.domain.usecase.GetInstitutions
import hr.wortex.otpstudent.domain.usecase.Register
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

sealed class RegisterUiState {
    object Loading : RegisterUiState()
    object Idle : RegisterUiState()
    data class Success(val userRegister: UserRegister) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegistrationScreenViewModel(private val registerUseCase: Register, private val getInstitutions: GetInstitutions): ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _institutions = MutableStateFlow<Map<Int, String>>(emptyMap())
    val institutions: StateFlow<Map<Int, String>> = _institutions

    init {
        loadInstitutions()
    }

    private fun loadInstitutions() {
        viewModelScope.launch {
            try {
                val list = getInstitutions()
                _institutions.value = list.associate { it.id to it.name }
            } catch (e: Exception) {
            }
        }
    }

    fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        hashPassword: String,
        yearOfStudy: Number,
        areaOfStudy: String,
        dateOfStudy: String,
        higherEducationBodyID: Int
    ){
        viewModelScope.launch {
            try {
                val result = registerUseCase.invoke(
                    firstName,
                    lastName,
                    email,
                    hashPassword,
                    yearOfStudy,
                    areaOfStudy,
                    dateOfStudy,
                    higherEducationBodyID
                )
                _uiState.value = RegisterUiState.Success(result)
            } catch (e: IOException) {
                _uiState.value = RegisterUiState.Error("Nema internetske veze. Provjerite mrežu.")
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error("Dogodila se greška: ${e.localizedMessage}")
            }
        }
    }
}

class RegistrationViewModelFactory(private val registrationUseCase: Register, private val institutionUseCase: GetInstitutions) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationScreenViewModel(registrationUseCase, institutionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}