package com.example.pplapp.ui.shipment

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pplapp.data.api.NetworkModule
import com.example.pplapp.data.api.PplApiService
import com.example.pplapp.data.model.*
import com.example.pplapp.data.storage.ShipmentStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CreateShipmentUiState(
    // Recipient Address
    val country: String = "Česká republika",
    val customerName: String = "",
    val street: String = "",
    val city: String = "",
    val zipCode: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    
    // Shipment Parameters
    val parcelCount: String = "1",
    val codAmount: String = "0",
    val codCurrency: String = "CZK",
    val variableSymbol: String = "",
    val printNote: String = "",
    val customerReference: String = "",

    // Request State
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val labelUrl: String? = null
)

class CreateShipmentViewModel : ViewModel() {
    private val _state = mutableStateOf(CreateShipmentUiState())
    val state: State<CreateShipmentUiState> = _state

    private val apiService = NetworkModule.createService(PplApiService::class.java)

    private val countryCodes = mapOf(
        "Česká republika" to "CZ",
        "Slovensko" to "SK",
        "Německo" to "DE",
        "Rakousko" to "AT",
        "Polsko" to "PL",
        "Francie" to "FR",
        "Itálie" to "IT",
        "Španělsko" to "ES"
    )

    fun createShipment() {
        val currentState = _state.value
        val capturedCustomerName = currentState.customerName // Důležité: zachytit jméno hned
        
        viewModelScope.launch {
            _state.value = currentState.copy(
                isLoading = true, 
                successMessage = null, 
                errorMessage = null,
                labelUrl = null
            )
            try {
                val request = CreateShipmentBatchRequest(
                    returnChannel = ReturnChannel(address = "zavolejmikote@gmail.com"),
                    labelSettings = LabelSettings(),
                    shipments = listOf(
                        ShipmentRequest(
                            referenceId = currentState.customerReference.ifBlank { "APP-${System.currentTimeMillis()}" },
                            note = "${currentState.printNote} ${currentState.customerReference}".trim(),
                            shipmentSet = ShipmentSet(numberOfShipments = currentState.parcelCount.toIntOrNull() ?: 1),
                            sender = SenderRequest(
                                name = "Tester",
                                street = "Ondrova 34",
                                city = "Brno",
                                zipCode = "63500",
                                country = "CZ"
                            ),
                            recipient = RecipientRequest(
                                name = currentState.customerName,
                                street = currentState.street,
                                city = currentState.city,
                                zipCode = currentState.zipCode,
                                country = countryCodes[currentState.country] ?: "CZ",
                                phone = currentState.phone,
                                email = currentState.email,
                                contact = currentState.contactPerson
                            ),
                            cashOnDelivery = if ((currentState.codAmount.toDoubleOrNull() ?: 0.0) > 0) {
                                CashOnDelivery(
                                    amount = currentState.codAmount.toDoubleOrNull() ?: 0.0,
                                    currency = currentState.codCurrency,
                                    variableSymbol = currentState.variableSymbol
                                )
                            } else null
                        )
                    )
                )

                val response = apiService.createShipmentBatch(request)
                
                if (response.isSuccessful) {
                    val locationHeader = response.headers()["Location"]
                    val batchId = locationHeader?.substringAfterLast("/")
                    
                    if (batchId != null) {
                        delay(2000)
                        fetchBatchResult(batchId, capturedCustomerName)
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Zásilka vytvořena, ale chybí ID dávky (Location header)."
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Chyba při odeslání: ${response.code()} ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Chyba při vytváření zásilky"
                )
            }
        }
    }

    private suspend fun fetchBatchResult(batchId: String, customerName: String) {
        Log.d("CreateShipmentVM", "Fetching results for batch: $batchId")
        try {
            val result = apiService.getBatchDetail(batchId)
            val shipment = result.shipments?.firstOrNull()
            
            if (shipment != null) {
                val shipmentNumber = shipment.shipmentNumber ?: ""
                Log.d("CreateShipmentVM", "Success! Shipment number: $shipmentNumber")
                
                // Uložit do lokálního seznamu (ShipmentStorage je Singleton)
                ShipmentStorage.addShipment(
                    LocalShipment(
                        shipmentNumber = shipmentNumber,
                        recipientName = customerName
                    )
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Zásilka vytvořena! Číslo: $shipmentNumber",
                    labelUrl = shipment.labelUrl
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Zásilka vytvořena, ale v detailu dávky nejsou žádná data."
                )
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                errorMessage = "Chyba při získávání detailu dávky: ${e.localizedMessage}"
            )
        }
    }

    fun onCountryChange(value: String) { _state.value = _state.value.copy(country = value) }
    fun onCustomerNameChange(value: String) { _state.value = _state.value.copy(customerName = value) }
    fun onStreetChange(value: String) { _state.value = _state.value.copy(street = value) }
    fun onCityChange(value: String) { _state.value = _state.value.copy(city = value) }
    fun onZipCodeChange(value: String) { _state.value = _state.value.copy(zipCode = value) }
    fun onContactPersonChange(value: String) { _state.value = _state.value.copy(contactPerson = value) }
    fun onPhoneChange(value: String) { _state.value = _state.value.copy(phone = value) }
    fun onEmailChange(value: String) { _state.value = _state.value.copy(email = value) }
    fun onParcelCountChange(value: String) { _state.value = _state.value.copy(parcelCount = value) }
    fun onCodAmountChange(value: String) { _state.value = _state.value.copy(codAmount = value) }
    fun onCodCurrencyChange(value: String) { _state.value = _state.value.copy(codCurrency = value) }
    fun onVariableSymbolChange(value: String) { _state.value = _state.value.copy(variableSymbol = value) }
    fun onPrintNoteChange(value: String) { _state.value = _state.value.copy(printNote = value) }
    fun onCustomerReferenceChange(value: String) { _state.value = _state.value.copy(customerReference = value) }
}
