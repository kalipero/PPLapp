package com.example.pplapp.data.model

data class LocalShipment(
    val shipmentNumber: String,
    val recipientName: String,
    val createdAt: Long = System.currentTimeMillis()
)
