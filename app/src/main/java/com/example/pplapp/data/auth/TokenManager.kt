package com.example.pplapp.data.auth

import com.example.pplapp.data.model.TokenResponse

object TokenManager {
    private var accessToken: String? = null
    private var expiresAt: Long = 0

    fun saveToken(response: TokenResponse) {
        accessToken = response.accessToken
        // Rezerva 60 sekund před skutečnou expirací
        expiresAt = System.currentTimeMillis() + (response.expiresIn * 1000L) - 60000L
    }

    fun getToken(): String? = accessToken

    fun isTokenValid(): Boolean {
        return accessToken != null && System.currentTimeMillis() < expiresAt
    }

    fun clearToken() {
        accessToken = null
        expiresAt = 0
    }
}
