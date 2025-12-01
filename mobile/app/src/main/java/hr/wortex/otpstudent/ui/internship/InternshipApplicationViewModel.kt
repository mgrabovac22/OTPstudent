package hr.wortex.otpstudent.ui.internship

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.InternshipApplication
import hr.wortex.otpstudent.domain.model.InternshipJob
import hr.wortex.otpstudent.domain.model.User
import hr.wortex.otpstudent.domain.repository.interfaces.IInternshipRepository
import hr.wortex.otpstudent.domain.repository.interfaces.IUserRepository
import hr.wortex.otpstudent.domain.usecase.ApplyToInternship
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

data class InternshipApplicationState(
    val user: User? = null,
    val availableJobs: List<InternshipJob> = emptyList(),
    val studentAddress: String = "",
    val contactNumber: String = "",
    val selectedJobIds: Set<Int> = emptySet(),
    val practiceDurationInWeeks: Int = 2,
    val practiceExpectations: String = "",
    val expectedStartDate: String = "",
    val expectedEndDate: String = "",
    val submissionState: SubmissionState = SubmissionState.Idle,
    val addressError: String? = null,
    val contactNumberError: String? = null,
    val jobsError: String? = null,
    val detailsError: String? = null
)

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Loading : SubmissionState()
    object Success : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}

class InternshipApplicationViewModel(
    private val userRepository: IUserRepository,
    private val internshipRepository: IInternshipRepository,
    private val applyToInternship: ApplyToInternship
) : ViewModel() {

    private val _uiState = MutableStateFlow(InternshipApplicationState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private val displayFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
        private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    init {
        fetchInitialData()
        fetchAvailableJobs()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()
                _uiState.update { it.copy(user = user) }
            } catch (e: Exception) {}
        }
    }

    private fun fetchAvailableJobs() {
        viewModelScope.launch {
            try {
                val jobs = internshipRepository.getInternshipJobs()
                _uiState.update { it.copy(availableJobs = jobs) }
            } catch (e: Exception) {}
        }
    }

    fun updateAddress(newAddress: String) {
        _uiState.update { it.copy(studentAddress = newAddress, addressError = null) }
    }

    fun updateContactNumber(newNumber: String) {
        _uiState.update { it.copy(contactNumber = newNumber, contactNumberError = null) }
    }

    fun toggleJobSelection(jobId: Int) {
        val currentSelected = _uiState.value.selectedJobIds.toMutableSet()
        if (jobId in currentSelected) currentSelected.remove(jobId) else currentSelected.add(jobId)
        _uiState.update { it.copy(selectedJobIds = currentSelected, jobsError = null) }
    }

    fun updateDuration(weeks: Int) {
        _uiState.update { it.copy(practiceDurationInWeeks = weeks) }
        if (_uiState.value.expectedStartDate.isNotEmpty()) {
            recalculateEndDate(_uiState.value.expectedStartDate, weeks)
        }
    }

    fun updateExpectations(text: String) {
        _uiState.update { it.copy(practiceExpectations = text, detailsError = null) }
    }

    fun updateStartDate(millis: Long) {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val startDateString = displayFormat.format(calendar.time)
        _uiState.update { it.copy(expectedStartDate = startDateString, detailsError = null) }
        recalculateEndDate(startDateString, _uiState.value.practiceDurationInWeeks)
    }

    private fun recalculateEndDate(startDateString: String, durationInWeeks: Int) {
        try {
            val startDate = displayFormat.parse(startDateString)
            val calendar = Calendar.getInstance().apply { time = startDate!! }
            calendar.add(Calendar.WEEK_OF_YEAR, durationInWeeks)
            val endDateString = displayFormat.format(calendar.time)
            _uiState.update { it.copy(expectedEndDate = endDateString) }
        } catch (e: Exception) {}
    }

    fun validateUserDataStep(): Boolean {
        val state = _uiState.value
        val addressError = if (state.studentAddress.trim().isEmpty()) "Adresa ne smije biti prazna." else null
        val contactNumberError = if (state.contactNumber.trim().isEmpty()) "Kontakt broj ne smije biti prazan." else null

        _uiState.update {
            it.copy(
                addressError = addressError,
                contactNumberError = contactNumberError
            )
        }
        return addressError == null && contactNumberError == null
    }

    fun validateJobsStep(): Boolean {
        val state = _uiState.value
        if (state.selectedJobIds.isEmpty()) {
            _uiState.update { it.copy(jobsError = "Odaberite barem jedno područje.") }
            return false
        }
        return true
    }

    fun validateDetailsStep(): Boolean {
        val state = _uiState.value
        if (state.expectedStartDate.isBlank() || state.practiceExpectations.isBlank()) {
            _uiState.update { it.copy(detailsError = "Sva polja su obavezna.") }
            return false
        }
        return true
    }

    fun submitApplication() {
        if (!validateDetailsStep() || !validateJobsStep() || !validateUserDataStep()) return

        viewModelScope.launch {
            _uiState.update { it.copy(submissionState = SubmissionState.Loading) }
            try {
                val currentState = _uiState.value

                val startDateForApi = displayFormat.parse(currentState.expectedStartDate)?.let { apiFormat.format(it) } ?: ""
                val endDateForApi = displayFormat.parse(currentState.expectedEndDate)?.let { apiFormat.format(it) } ?: ""

                val application = InternshipApplication(
                    id = 0,
                    studentExpectations = currentState.practiceExpectations,
                    studentAdress = currentState.studentAddress,
                    contactNumber = currentState.contactNumber,
                    dateOfApplication = LocalDate.now().toString(),
                    duration = currentState.practiceDurationInWeeks,
                    expectedStartDate = startDateForApi,
                    expectedEndDate = endDateForApi,
                    expectedJobs = currentState.selectedJobIds.toList()
                )
                
                val success = applyToInternship(application)
                
                if (success) {
                    _uiState.update { it.copy(submissionState = SubmissionState.Success) }
                } else {
                    _uiState.update { it.copy(submissionState = SubmissionState.Error("Prijava nije uspjela. Molimo pokušajte ponovo.")) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(submissionState = SubmissionState.Error(e.message ?: "Došlo je do neočekivane greške.")) }
            }
        }
    }
}

class InternshipApplicationViewModelFactory(
    private val userRepository: IUserRepository,
    private val internshipRepository: IInternshipRepository,
    private val applyToInternship: ApplyToInternship
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternshipApplicationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InternshipApplicationViewModel(userRepository, internshipRepository, applyToInternship) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
