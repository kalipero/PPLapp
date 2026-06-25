package com.example.pplapp.data.api

import com.example.pplapp.data.model.ShipmentResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PplApiService {
    @GET("shipment")
    suspend fun getShipment(
        @Query("limit") limit: Int = 1000,
        @Query("offset") offset: Int = 0,
        @Query("ShipmentNumbers") shipmentNumber: String
    ): List<ShipmentResponse>
}
