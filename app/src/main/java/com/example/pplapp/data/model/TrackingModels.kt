package com.example.pplapp.data.model

import com.google.gson.annotations.SerializedName

data class ShipmentResponse(
    @SerializedName("shipmentNumber")
    val shipmentNumber: String,
    val status: String? = null,
    @SerializedName("trackAndTrace")
    val trackAndTrace: TrackAndTrace? = null
)

data class TrackAndTrace(
    @SerializedName("events")
    val events: List<TrackingEvent>? = emptyList()
)

data class TrackingEvent(
    @SerializedName("eventDate")
    val timestamp: String,
    val location: String? = null,
    val name: String,
    @SerializedName("code")
    val statusCode: String? = null
)
