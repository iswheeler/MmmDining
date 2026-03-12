package dev.altavision.mdininginfo.presentation

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

sealed class DetailsUiState {
    object Loading : DetailsUiState()
    data class Success(val responseWrapper: MenuResponseWrapper) : DetailsUiState()
    data class Error(val message : String) : DetailsUiState()
}

class DetailsViewModel(_locationName: String) : ViewModel() {
    private val _detailsUiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading);
    val detailsUiState: StateFlow<DetailsUiState> = _detailsUiState;
    val locationName : String = _locationName;
    var meal : String = "LUNCH";

    init {
        fetchMenu();
    }

    private fun fetchMenu() {
        viewModelScope.launch {
            _detailsUiState.value = DetailsUiState.Loading
            try {
                // GOLDEN:https://prod-michigan-dining-services-prod.apps.containersprod.art2.p1.openshiftapps.com/dining/menu?key=093665d6ab069c859267fd4001c3c562ba805539ed852978&location=Bursley%20Dining%20Hall&date=11-03-2026&meal=LUNCH
                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                val currentDate = sdf.format(Date())
                meal = if (currentHour < 10) "BREAKFAST" else if (currentHour < 17) "LUNCH" else "DINNER";
                val responseWrapper = RetrofitInstance.api.getMenu(date = currentDate, location = locationName, meal = meal); // TODO: Add breakfast/dinner
                _detailsUiState.value = DetailsUiState.Success(responseWrapper);
            } catch (e: Exception) {
                _detailsUiState.value = DetailsUiState.Error(e.message ?: "Error occurred");
            }
        }
    }
}

class DetailsViewModelFactory(private val name: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(name) as T
    }
}