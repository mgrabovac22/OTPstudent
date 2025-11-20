package hr.wortex.otpstudent.ui.profil.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.model.UserProfile
import hr.wortex.otpstudent.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Stanje koje drži podatke forme
data class EditProfileState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    // Polja forme
    val firstName: String = "",
    val lastName: String = "",
    val yearOfStudy: String = "", // String radi lakšeg unosa u TextField
    val areaOfStudy: String = "",
    val dateOfBirth: String = "",

    // Read-only podaci za prikaz (npr. slika, email)
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
                        dateOfBirth = user.dateOfBirth ?: "",
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

    // Metode za ažuriranje stanja forme kad korisnik tipka
    fun onFirstNameChange(newValue: String) {
        _uiState.update { it.copy(firstName = newValue) }
    }

    fun onLastNameChange(newValue: String) {
        _uiState.update { it.copy(lastName = newValue) }
    }

    fun onYearOfStudyChange(newValue: String) {
        // Dozvoli samo brojeve ako želiš, ili ostavi kao string pa parsiraj kod spremanja
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

                // Parsiranje godine studija
                val yearInt = currentState.yearOfStudy.toIntOrNull() ?: 0

                val domainUser = User(
                    firstName = currentState.firstName,
                    lastName = currentState.lastName,
                    email = currentState.email, // Email se obično ne mijenja ovdje, ali ga šaljemo
                    yearOfStudy = yearInt,
                    areaOfStudy = currentState.areaOfStudy,
                    dateOfBirth = currentState.dateOfBirth,
                    imagePath = null, // Ne mijenjamo path
                    cvPath = currentState.cvPath,
                    image = null // Ne šaljemo base64 natrag na update
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

    // Resetiranje statusa spremanja da se navigacija ne bi okinula više puta
    fun onSavedHandled() {
        _uiState.update { it.copy(isSaved = false) }
    }
}