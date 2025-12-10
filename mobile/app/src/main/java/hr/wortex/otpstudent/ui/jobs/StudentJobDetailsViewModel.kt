package hr.wortex.otpstudent.ui.career

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.StudentJobDetail
import hr.wortex.otpstudent.domain.usecase.ApplyToStudentJob
import hr.wortex.otpstudent.domain.usecase.GetStudentJobDetails
import hr.wortex.otpstudent.domain.usecase.HasStudentAppliedToJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentJobDetailsUiState(
    val isLoading: Boolean = false,
    val job: StudentJobDetail? = null,
    val error: String? = null,
    val isApplying: Boolean = false,
    val applySuccess: Boolean? = null,
    val isApplied: Boolean = false
)

class StudentJobDetailsViewModel(
    private val jobId: Int,
    private val getStudentJobDetails: GetStudentJobDetails,
    private val applyToStudentJob: ApplyToStudentJob,
    private val hasStudentAppliedToJob: HasStudentAppliedToJob
) : ViewModel() {


    private val _uiState = MutableStateFlow(
        StudentJobDetailsUiState(isLoading = true)
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadJobDetails()
    }

    private fun loadJobDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = getStudentJobDetails(jobId)
                val alreadyApplied = hasStudentAppliedToJob(jobId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        job = details,
                        error = null,
                        isApplied = alreadyApplied
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Došlo je do greške pri učitavanju posla."
                    )
                }
            }
        }
    }

    fun apply() {
        val currentState = _uiState.value
        val currentJob = currentState.job ?: return
        if (currentState.isApplied) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isApplying = true,
                    applySuccess = null
                )
            }
            try {
                val success = applyToStudentJob(currentJob.id)
                _uiState.update {
                    it.copy(
                        isApplying = false,
                        applySuccess = success,
                        isApplied = if (success) true else it.isApplied
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isApplying = false,
                        applySuccess = false,
                        error = e.message ?: "Prijava nije uspjela."
                    )
                }
            }
        }
    }
}

class StudentJobDetailsViewModelFactory(
    private val jobId: Int,
    private val getStudentJobDetails: GetStudentJobDetails,
    private val applyToStudentJob: ApplyToStudentJob,
    private val hasStudentAppliedToJob: HasStudentAppliedToJob
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentJobDetailsViewModel::class.java)) {
            return StudentJobDetailsViewModel(
                jobId = jobId,
                getStudentJobDetails = getStudentJobDetails,
                applyToStudentJob = applyToStudentJob,
                hasStudentAppliedToJob = hasStudentAppliedToJob
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}