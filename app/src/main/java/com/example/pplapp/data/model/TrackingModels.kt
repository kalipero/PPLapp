package com.example.pplapp.data.model

import com.google.gson.annotations.SerializedName

data class ShipmentResponse(
    @SerializedName("shipmentNumber")
    val shipmentNumber: String,
    val status: String? = null,
    val events: List<TrackingEvent> = emptyList()
)

data class TrackingEvent(
    val timestamp: String,
    val location: String? = null,
    val description: String,
    @SerializedName("statusCode")
    val statusCode: String? = null
)
