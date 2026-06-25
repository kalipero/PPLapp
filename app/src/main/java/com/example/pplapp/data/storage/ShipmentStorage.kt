package com.example.pplapp.data.storage

import android.util.Log
import com.example.pplapp.data.model.LocalShipment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

object ShipmentStorage {
    private val _shipments = MutableStateFlow<List<LocalShipment>>(emptyList())
    val shipments: StateFlow<List<LocalShipment>> = _shipments.asStateFlow()

    fun addShipment(shipment: LocalShipment) {
        Log.d("ShipmentStorage", "Adding shipment: ${shipment.shipmentNumber}")
        _shipments.update { current ->
            listOf(shipment) + current
        }
        Log.d("ShipmentStorage", "Current list size: ${_shipments.value.size}")
    }
}
