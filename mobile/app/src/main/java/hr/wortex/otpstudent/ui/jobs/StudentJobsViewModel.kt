package hr.wortex.otpstudent.ui.career

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.StudentJob
import hr.wortex.otpstudent.domain.usecase.GetStudentJobs
import kotlinx.coroutines.launch

enum class JobFilterType {
    APPLIED,
    ALL,
    NOT_APPLIED
}

data class StudentJobsUiState(
    val isLoading: Boolean = false,
    val jobs: List<StudentJob> = emptyList(),
    val error: String? = null,
    val selectedFilter: JobFilterType = JobFilterType.ALL
)

class StudentJobsViewModel(
    private val getStudentJobs: GetStudentJobs
) : ViewModel() {

    var uiState by mutableStateOf(StudentJobsUiState())
        private set

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val jobs = getStudentJobs()
                uiState = uiState.copy(
                    isLoading = false,
                    jobs = jobs,
                    error = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Došlo je do greške pri učitavanju poslova."
                )
            }
        }
    }

    fun onFilterSelected(filter: JobFilterType) {
        uiState = uiState.copy(selectedFilter = filter)
    }
}

class StudentJobsViewModelFactory(
    private val getStudentJobs: GetStudentJobs
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentJobsViewModel::class.java)) {
            return StudentJobsViewModel(getStudentJobs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}