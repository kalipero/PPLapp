package com.example.pplapp.ui.tracking

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pplapp.data.api.NetworkModule
import com.example.pplapp.data.api.PplApiService
import com.example.pplapp.data.model.ShipmentResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch

sealed class TrackingUiState {
    object Idle : TrackingUiState()
    object Loading : TrackingUiState()
    data class Success(val shipment: ShipmentResponse) : TrackingUiState()
    data class Error(val message: String) : TrackingUiState()
}

class TrackingViewModel : ViewModel() {
    private val _uiState = mutableStateOf<TrackingUiState>(TrackingUiState.Idle)
    val uiState: State<TrackingUiState> = _uiState

    private val apiService = NetworkModule.createService(PplApiService::class.java)

    fun searchShipment(shipmentNumber: String) {
        if (shipmentNumber.isBlank()) return

        viewModelScope.launch {
            _uiState.value = TrackingUiState.Loading
            try {
                // Voláme API s parametry: limit=1000, offset=0
                // Vracíme první zásilku ze seznamu (protože API vrací List)
                val resultList = apiService.getShipment(
                    limit = 1000,
                    offset = 0,
                    shipmentNumber = shipmentNumber
                )
                val result = resultList.firstOrNull()
                
                if (result != null) {
                    _uiState.value = TrackingUiState.Success(result)
                } else {
                    _uiState.value = TrackingUiState.Error("Zásilka nenalezena")
                }
            } catch (e: Exception) {
                _uiState.value = TrackingUiState.Error(e.localizedMessage ?: "Neznámá chyba")
            }
        }
    }
}
