package com.example.pplapp.data.model

import com.google.gson.annotations.SerializedName

data class CreateShipmentBatchRequest(
    val returnChannel: ReturnChannel,
    val labelSettings: LabelSettings,
    val shipments: List<ShipmentRequest>
)

data class ReturnChannel(
    val type: String = "Email",
    val address: String
)

data class LabelSettings(
    val format: String = "Pdf",
    val dpi: Int = 300,
    val completeLabelSettings: CompleteLabelSettings = CompleteLabelSettings()
)

data class CompleteLabelSettings(
    val isCompleteLabelRequested: Boolean = false
)

data class ShipmentRequest(
    val referenceId: String,
    val productType: String = "BUSS",
    val note: String,
    val shipmentSet: ShipmentSet,
    val sender: SenderRequest,
    val recipient: RecipientRequest,
    val cashOnDelivery: CashOnDelivery? = null
)

data class SenderRequest(
    val name: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

data class ShipmentSet(
    val numberOfShipments: Int
)

data class RecipientRequest(
    val name: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String,
    val phone: String,
    val email: String,
    val contact: String
)

data class CashOnDelivery(
    val amount: Double,
    val currency: String,
    val variableSymbol: String
)

data class CreateShipmentResponse(
    val batchId: String? = null,
    val shipments: List<ShipmentIdResponse>? = null
)

data class ShipmentIdResponse(
    val shipmentNumber: String? = null,
    val referenceId: String? = null,
    val labelUrl: String? = null
)
