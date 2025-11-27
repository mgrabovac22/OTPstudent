package hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.InfoContent
import hr.wortex.otpstudent.domain.usecase.GetInfoContentDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class InfoDetailUiState {
    object Loading : InfoDetailUiState()
    data class Success(val infoContent: InfoContent) : InfoDetailUiState()
    data class Error(val message: String) : InfoDetailUiState()
}

class InfoDetailViewModel(
    private val getInfoContentDetailUseCase: GetInfoContentDetail
) : ViewModel() {

    private val _uiState = MutableStateFlow<InfoDetailUiState>(InfoDetailUiState.Loading)

    val uiState: StateFlow<InfoDetailUiState> = _uiState

    fun fetchInfoDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = InfoDetailUiState.Loading
            try {
                val content = getInfoContentDetailUseCase(id)
                _uiState.value = InfoDetailUiState.Success(content)
            } catch (e: Exception) {
                _uiState.value = InfoDetailUiState.Error(e.message ?: "Greška pri dohvatu detalja")
            }
        }
    }
}