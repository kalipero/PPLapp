package com.example.pplapp.data.api

import com.example.pplapp.data.model.CreateShipmentBatchRequest
import com.example.pplapp.data.model.CreateShipmentResponse
import com.example.pplapp.data.model.ShipmentResponse
import retrofit2.Response
import retrofit2.http.*

interface PplApiService {
    @GET("shipment")
    suspend fun getShipment(
        @Query("limit") limit: Int = 1000,
        @Query("offset") offset: Int = 0,
        @Query("ShipmentNumbers") shipmentNumber: String
    ): List<ShipmentResponse>

    @POST("shipment/batch")
    suspend fun createShipmentBatch(
        @Body request: CreateShipmentBatchRequest
    ): Response<CreateShipmentResponse>

    @GET("shipment/batch/{batchId}")
    suspend fun getBatchDetail(
        @Path("batchId") batchId: String
    ): CreateShipmentResponse
}
