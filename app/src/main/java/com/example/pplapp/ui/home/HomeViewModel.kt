package com.example.pplapp.ui.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pplapp.data.api.NetworkModule
import com.example.pplapp.data.api.PplAuthApi
import com.example.pplapp.data.auth.TokenManager
import com.example.pplapp.data.model.LocalShipment
import com.example.pplapp.data.storage.ShipmentStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val message: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {
    private val _uiState = mutableStateOf<HomeUiState>(HomeUiState.Idle)
    val uiState: State<HomeUiState> = _uiState

    val createdShipments: StateFlow<List<LocalShipment>> = ShipmentStorage.shipments

    init {
        viewModelScope.launch {
            createdShipments.collect {
                Log.d("HomeVM", "Shipments updated in VM. New count: ${it.size}")
            }
        }
    }

    private val authApi = NetworkModule.createService(PplAuthApi::class.java)

    // TODO: V produkci by měly být tyto údaje v bezpečnějším úložišti
    private val clientId = "EB2050937"
    private val clientSecret = "KWC3WwInmpe0LR5Zgpvd971KxLscWO8I"

    fun refreshToken() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Vynutíme vymazání starého tokenu
                TokenManager.clearToken()

                val response = withContext(Dispatchers.IO) {
                    authApi.getToken(
                        clientId = clientId,
                        clientSecret = clientSecret
                    ).execute()
                }

                if (response.isSuccessful) {
                    response.body()?.let {
                        TokenManager.saveToken(it)
                        _uiState.value = HomeUiState.Success("Token byl úspěšně obnoven!")
                        Log.d("PPL_AUTH", "Token manually refreshed: ${it.accessToken}")
                    }
                } else {
                    val errorMsg = "Chyba ${response.code()}: ${response.errorBody()?.string()}"
                    _uiState.value = HomeUiState.Error(errorMsg)
                    Log.e("PPL_AUTH", errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Neznámá chyba při komunikaci"
                _uiState.value = HomeUiState.Error(errorMsg)
                Log.e("PPL_AUTH", errorMsg, e)
            }
        }
    }
    
    fun resetState() {
        _uiState.value = HomeUiState.Idle
    }
}
