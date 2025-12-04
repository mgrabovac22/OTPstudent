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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convertMillisToDisplayDate(millis: Long): String {
        return displayFormat.format(Date(millis))
    }

    fun formatForDisplay(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val date = when {
                dateString.contains("T") -> isoFormat.parse(dateString.split(".")[0])
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

    fun latestAllowedBirthDateMillis(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -18)
        return cal.timeInMillis
    }

    fun todayMillis(): Long = Calendar.getInstance().timeInMillis

    fun isAtLeast18(displayDate: String): Boolean {
        return try {
            val dob = displayFormat.parse(displayDate) ?: return false
            val calDob = Calendar.getInstance().apply { time = dob }
            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - calDob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < calDob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age >= 18
        } catch (e: Exception) {
            false
        }
    }

    fun isNotInFuture(displayDate: String): Boolean {
        return try {
            val dob = displayFormat.parse(displayDate) ?: return false
            dob.time <= todayMillis()
        } catch (e: Exception) {
            false
        }
    }

    fun displayToMillis(displayString: String): Long? {
        return try {
            displayFormat.parse(displayString)?.time
        } catch (e: Exception) {
            null
        }
    }
}

data class EditProfileState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    val userId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val yearOfStudy: String = "",
    val areaOfStudy: String = "",
    val dateOfBirth: String = "",

    val email: String = "",
    val imageBase64: String? = null,
    val cvPath: String? = null,
    val experiencePoints: Int = 0,
    val unlockedLevel: Int = 1,

    val imageError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val yearOfStudyError: String? = null,
    val areaOfStudyError: String? = null,
    val dateOfBirthError: String? = null,
    val emailError: String? = null,

    val isChangePasswordModalVisible: Boolean = true,
    val oldPasswordInput: String = "",
    val newPasswordInput: String = "",
    val newPasswordConfirmInput: String = "",

    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val newPasswordConfirmError: String? = null,
    val changePasswordErrorMessage: String? = null,
    val isPasswordChanged: Boolean = false
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
                        userId = user.id,
                        firstName = user.firstName ?: "",
                        lastName = user.lastName ?: "",
                        yearOfStudy = user.yearOfStudy?.toString() ?: "",
                        areaOfStudy = user.areaOfStudy ?: "",
                        dateOfBirth = DateUtils.formatForDisplay(user.dateOfBirth),
                        email = user.email ?: "",
                        imageBase64 = user.image,
                        cvPath = user.cvPath,

                        firstNameError = null,
                        lastNameError = null,
                        yearOfStudyError = null,
                        areaOfStudyError = null,
                        dateOfBirthError = null,
                        emailError = null,
                        errorMessage = null,
                        imageError = null
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
        _uiState.update { it.copy(firstName = newValue, firstNameError = null) }
    }

    fun onLastNameChange(newValue: String) {
        _uiState.update { it.copy(lastName = newValue, lastNameError = null) }
    }

    fun onYearOfStudyChange(newValue: String) {
        _uiState.update { it.copy(yearOfStudy = newValue, yearOfStudyError = null) }
    }

    fun onAreaOfStudyChange(newValue: String) {
        _uiState.update { it.copy(areaOfStudy = newValue, areaOfStudyError = null) }
    }

    fun onDateOfBirthChange(newValue: String) {
        _uiState.update { it.copy(dateOfBirth = newValue, dateOfBirthError = null) }
    }

    fun onDateSelected(millis: Long?) {
        millis?.let {
            val formattedDate = DateUtils.convertMillisToDisplayDate(it)
            _uiState.update { state -> state.copy(dateOfBirth = formattedDate, dateOfBirthError = null) }
        }
    }

    fun uploadImage(inputStream: InputStream, fileName: String, mimeType: String) {
        viewModelScope.launch {

            val lowerName = fileName.lowercase(Locale.ROOT)
            val lowerMime = mimeType.lowercase(Locale.ROOT)

            val isJpgOrPng = lowerName.endsWith(".jpg") ||
                    lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".png") ||
                    lowerMime == "image/jpg" ||
                    lowerMime == "image/jpeg" ||
                    lowerMime == "image/png"

            if (!isJpgOrPng) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        imageError = "Dozvoljeni formati slike su JPG i PNG.",
                        errorMessage = null
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            try {
                val bytes = inputStream.readBytes()

                val finalMimeType = when {
                    lowerName.endsWith(".png") || lowerMime == "image/png" -> "image/png"
                    else -> "image/jpeg"
                }

                val requestBody = bytes.toRequestBody(finalMimeType.toMediaTypeOrNull())

                val part = MultipartBody.Part.createFormData(
                    "image",
                    fileName,
                    requestBody
                )

                val success = userRepository.uploadImage(part)

                if (success) {
                    _uiState.update { it.copy(imageError = null) }
                    loadUserData()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            imageError = "Upload slike nije uspio."
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Greška: ${e.message}") }
            }
        }
    }

    private fun validateAndUpdateState(): Boolean {
        val state = _uiState.value

        var firstNameError: String? = null
        var lastNameError: String? = null
        var yearOfStudyError: String? = null
        var areaOfStudyError: String? = null
        var dateOfBirthError: String? = null
        var emailError: String? = null

        var hasError = false

        val firstName = state.firstName.trim()
        if (firstName.isEmpty()) {
            firstNameError = "Unesi ime."
            hasError = true
        } else if (firstName.length < 2) {
            firstNameError = "Ime mora imati barem 2 slova."
            hasError = true
        }

        val lastName = state.lastName.trim()
        if (lastName.isEmpty()) {
            lastNameError = "Unesi prezime."
            hasError = true
        } else if (lastName.length < 2) {
            lastNameError = "Prezime mora imati barem 2 slova."
            hasError = true
        }

        val email = state.email.trim()
        if (email.isEmpty()) {
            emailError = "Email nedostaje."
            hasError = true
        } else if (!email.endsWith("@student.foi.hr")) {
            emailError = "Email mora završavati s @student.foi.hr."
            hasError = true
        }

        val year = state.yearOfStudy.toIntOrNull()
        if (year == null) {
            yearOfStudyError = "Godina studija mora biti broj."
            hasError = true
        } else if (year !in 1..8) {
            yearOfStudyError = "Godina studija mora biti između 1 i 8."
            hasError = true
        }

        val area = state.areaOfStudy.trim()
        if (area.isEmpty()) {
            areaOfStudyError = "Unesi smjer studija."
            hasError = true
        }

        val dob = state.dateOfBirth.trim()
        if (dob.isEmpty()) {
            dateOfBirthError = "Unesi datum rođenja."
            hasError = true
        } else {
            if (!DateUtils.isNotInFuture(dob)) {
                dateOfBirthError = "Datum rođenja ne može biti u budućnosti."
                hasError = true
            } else if (!DateUtils.isAtLeast18(dob)) {
                dateOfBirthError = "Moraš imati najmanje 18 godina."
                hasError = true
            }
        }

        _uiState.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                yearOfStudyError = yearOfStudyError,
                areaOfStudyError = areaOfStudyError,
                dateOfBirthError = dateOfBirthError,
                emailError = emailError,
                errorMessage = null

            )
        }

        return !hasError
    }

    fun showPasswordDialog() {
        _uiState.update { it.copy(isChangePasswordModalVisible = true) }
    }

    fun hidePasswordDialog() {
        _uiState.update {
            it.copy(
                isChangePasswordModalVisible = false,
                oldPasswordInput = "",
                newPasswordInput = "",
                newPasswordConfirmInput = "",
                oldPasswordError = null,
                newPasswordError = null,
                newPasswordConfirmError = null,
                changePasswordErrorMessage = null,
                isPasswordChanged = false
            )
        }
    }

    fun onOldPasswordChange(v: String) {
        _uiState.update { it.copy(oldPasswordInput = v, oldPasswordError = null) }
    }

    fun onNewPasswordChange(v: String) {
        _uiState.update { it.copy(newPasswordInput = v, newPasswordError = null) }
    }

    fun onNewPasswordConfirmChange(v: String) {
        _uiState.update { it.copy(newPasswordConfirmInput = v, newPasswordConfirmError = null) }
    }

    fun confirmPasswordChange() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    changePasswordErrorMessage = null,
                    oldPasswordError = null,
                    newPasswordError = null,
                    newPasswordConfirmError = null
                )
            }

            val state = _uiState.value

            if (state.newPasswordInput != state.newPasswordConfirmInput) {
                _uiState.update {
                    it.copy(
                        newPasswordConfirmError = "Lozinke se ne podudaraju.",
                        isLoading = false
                    )
                }
                return@launch
            }

            try {
                val resp = userRepository.changePassword(
                    oldPassword = state.oldPasswordInput,
                    password = state.newPasswordInput
                )

                val code = resp.code()
                val errorText = resp.errorBody()?.string()

                when (code) {

                    200 -> {
                        _uiState.update {
                            it.copy(
                                isPasswordChanged = true,
                                isLoading = false,
                                isChangePasswordModalVisible = true
                            )
                        }
                    }

                    400 -> {
                        _uiState.update {
                            it.copy(
                                changePasswordErrorMessage = "Nisu uneseni svi potrebni podaci.",
                                isLoading = false
                            )
                        }
                    }

                    401 -> {
                        _uiState.update {
                            it.copy(
                                oldPasswordError = "Stara lozinka nije točna.",
                                isLoading = false
                            )
                        }
                    }

                    404 -> {
                        _uiState.update {
                            it.copy(
                                changePasswordErrorMessage = "Korisnik nije pronađen.",
                                isLoading = false
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                changePasswordErrorMessage =
                                errorText ?: "Dogodila se nepoznata pogreška.",
                                isLoading = false
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        changePasswordErrorMessage = "Greška s mrežom. Provjerite vezu.",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (!validateAndUpdateState()) {
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val currentState = _uiState.value
                val yearInt = currentState.yearOfStudy.toIntOrNull() ?: 0
                val apiDateOfBirth = DateUtils.formatForApi(currentState.dateOfBirth)

                val domainUser = User(
                    id = currentState.userId,
                    firstName = currentState.firstName.trim(),
                    lastName = currentState.lastName.trim(),
                    email = currentState.email.trim(),
                    yearOfStudy = yearInt,
                    areaOfStudy = currentState.areaOfStudy.trim(),
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

