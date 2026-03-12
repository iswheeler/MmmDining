package dev.altavision.mdininginfo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val diningLocations: List<DiningLocation>) : UiState()
    data class Error(val message : String) : UiState()
}

class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading);
    val uiState: StateFlow<UiState> = _uiState;

    init {
        fetchLocations();
    }

    private fun fetchLocations() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val locations = RetrofitInstance.api.getLocations();
                _uiState.value = UiState.Success(locations.filter { it.campus == "DINING HALLS"});
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error occurred");
            }
        }
    }
}