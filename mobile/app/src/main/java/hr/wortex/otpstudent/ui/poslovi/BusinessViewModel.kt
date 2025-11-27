package hr.wortex.otpstudent.ui.poslovi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.InternshipApplication
import hr.wortex.otpstudent.domain.usecase.ApplyToInternship
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class BusinessUiState {
    object Idle : BusinessUiState()
    object Loading : BusinessUiState()
    object Success : BusinessUiState()
    data class Error(val message: String) : BusinessUiState()
}

class BusinessViewModel(private val applyToInternship: ApplyToInternship) : ViewModel() {

    private val _uiState = MutableStateFlow<BusinessUiState>(BusinessUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun applyForInternship() {
        viewModelScope.launch {
            _uiState.value = BusinessUiState.Loading
            try {
                val dummyApplication = InternshipApplication(
                    id = 0,
                    studentExpectations = "Želim naučiti nove tehnologije.",
                    studentAdress = "Ilica 1, 10000 Zagreb",
                    contactNumber = "0912345678",
                    dateOfApplication = LocalDate.now().toString(),
                    duration = 3,
                    expectedStartDate = "2026-02-01",
                    expectedEndDate = "2026-08-01",
                    expectedJobs = listOf(1, 4) //
                )

                val ok = applyToInternship(dummyApplication)
                _uiState.value = if (ok) BusinessUiState.Success else BusinessUiState.Error("Prijava nije uspjela.")
            } catch (e: Exception) {
                _uiState.value = BusinessUiState.Error(e.message ?: "Nepoznata greška prilikom prijave.")
            }
        }
    }

    fun resetState() {
        _uiState.value = BusinessUiState.Idle
    }
}

class BusinessViewModelFactory(private val applyToInternship: ApplyToInternship) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessViewModel(applyToInternship) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}