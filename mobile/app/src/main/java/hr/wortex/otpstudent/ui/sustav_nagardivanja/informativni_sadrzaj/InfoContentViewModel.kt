package hr.wortex.otpstudent.ui.sustav_nagardivanja.informativni_sadrzaj

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.wortex.otpstudent.domain.model.InfoContent
import hr.wortex.otpstudent.domain.usecase.GetInfoContent
import hr.wortex.otpstudent.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class InfoContentUiState {
    object Loading : InfoContentUiState()
    data class Success(val infoContents : List<InfoContent>) : InfoContentUiState()
    data class Error(val message : String) : InfoContentUiState()
}
class InfoContentViewModel (
    private val getInfoContentUseCase: GetInfoContent
) : ViewModel() {
    private val _uiState = MutableStateFlow<InfoContentUiState>(InfoContentUiState.Loading)

    val uiState: StateFlow<InfoContentUiState> = _uiState

    init {
        fetchInfoContent()
    }

    fun fetchInfoContent() {
        viewModelScope.launch {
            _uiState.value = InfoContentUiState.Loading
            try {
                val contentList = getInfoContentUseCase()
                _uiState.value = InfoContentUiState.Success(contentList)
            } catch (e: Exception) {
                _uiState.value = InfoContentUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}