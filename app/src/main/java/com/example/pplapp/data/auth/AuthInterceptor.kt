package com.example.pplapp.data.auth

import android.util.Log
import com.example.pplapp.data.api.PplAuthApi
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Provider

class AuthInterceptor(
    private val authApiProvider: Provider<PplAuthApi>,
    private val clientId: String,
    private val clientSecret: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Ignorujeme login endpoint
        if (originalRequest.url.encodedPath.contains("login/getAccessToken")) {
            return chain.proceed(originalRequest)
        }

        synchronized(this) {
            if (!TokenManager.isTokenValid()) {
                Log.d("PPL_AUTH", "Token expired or missing. Fetching new one...")
                try {
                    val response = authApiProvider.get().getToken(
                        clientId = clientId,
                        clientSecret = clientSecret
                    ).execute()

                    if (response.isSuccessful) {
                        response.body()?.let {
                            TokenManager.saveToken(it)
                            Log.d("PPL_AUTH", "Token fetched successfully.")
                        }
                    } else {
                        Log.e("PPL_AUTH", "Failed to fetch token: ${response.code()} ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("PPL_AUTH", "Exception during token fetch", e)
                }
            }
        }

        val token = TokenManager.getToken()
        val finalRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(finalRequest)
        if (response.code == 401) {
            Log.w("PPL_AUTH", "Received 401. Clearing token.")
            TokenManager.clearToken()
        }
        return response
    }
}
