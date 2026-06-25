package com.example.pplapp.data.api

import com.example.pplapp.data.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PplAuthApi {
    @FormUrlEncoded
    @POST("login/getAccessToken")
    fun getToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("scope") scope: String = "myapi2"
    ): Call<TokenResponse>
}
