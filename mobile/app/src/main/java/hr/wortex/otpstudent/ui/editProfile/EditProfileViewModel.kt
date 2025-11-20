package hr.wortex.otpstudent.ui.profil.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun formatForDisplay(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val date = when {
                dateString.contains("T") -> isoFormat.parse(dateString.split(".")[0]) // Očisti milisekunde ako smetaju
                else -> apiFormat.parse(dateString)
            }
            if (date != null) displayFormat.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatForApi(displayString: String): String {
        return try {
            val date = displayFormat.parse(displayString)
            if (date != null) apiFormat.format(date) else displayString
        } catch (e: Exception) {
            displayString
        }
    }
}

data class EditProfileState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    val firstName: String = "",
    val lastName: String = "",
    val yearOfStudy: String = "",
    val areaOfStudy: String = "",
    val dateOfBirth: String = "",

    val email: String = "",
    val imageBase64: String? = null,
    val cvPath: String? = null
)

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState(isLoading = true))
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.getCurrentUser()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        firstName = user.firstName ?: "",
                        lastName = user.lastName ?: "",
                        yearOfStudy = user.yearOfStudy?.toString() ?: "",
                        areaOfStudy = user.areaOfStudy ?: "",

                        dateOfBirth = DateUtils.formatForDisplay(user.dateOfBirth),

                        email = user.email ?: "",
                        imageBase64 = user.image,
                        cvPath = user.cvPath
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Ne mogu učitati podatke: ${e.message}")
                }
            }
        }
    }

    fun onFirstNameChange(newValue: String) {
        _uiState.update { it.copy(firstName = newValue) }
    }

    fun onLastNameChange(newValue: String) {
        _uiState.update { it.copy(lastName = newValue) }
    }

    fun onYearOfStudyChange(newValue: String) {
        _uiState.update { it.copy(yearOfStudy = newValue) }
    }

    fun onAreaOfStudyChange(newValue: String) {
        _uiState.update { it.copy(areaOfStudy = newValue) }
    }

    fun onDateOfBirthChange(newValue: String) {
        _uiState.update { it.copy(dateOfBirth = newValue) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val currentState = _uiState.value
                val yearInt = currentState.yearOfStudy.toIntOrNull() ?: 0

                val apiDateOfBirth = DateUtils.formatForApi(currentState.dateOfBirth)

                val domainUser = User(
                    firstName = currentState.firstName,
                    lastName = currentState.lastName,
                    email = currentState.email,
                    yearOfStudy = yearInt,
                    areaOfStudy = currentState.areaOfStudy,

                    dateOfBirth = apiDateOfBirth,

                    imagePath = null,
                    cvPath = currentState.cvPath,
                    image = null
                )

                val success = userRepository.updateUser(domainUser)

                if (success) {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Greška prilikom spremanja.") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Neočekivana greška.") }
            }
        }
    }

    fun onSavedHandled() {
        _uiState.update { it.copy(isSaved = false) }
    }
}