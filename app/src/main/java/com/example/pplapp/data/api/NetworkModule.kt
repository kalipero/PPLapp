package com.example.pplapp.data.api

import com.example.pplapp.data.auth.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider

object NetworkModule {
    private const val BASE_URL_PROD = "https://api.dhl.com/ecs/ppl/myapi2/"
    
    // Změňte na BASE_URL_PROD pro produkci
    private const val BASE_URL = BASE_URL_PROD

    // TODO: Nahraďte svými údaji
    private const val CLIENT_ID = "EB2050937"
    private const val CLIENT_SECRET = "KWC3WwInmpe0LR5Zgpvd971KxLscWO8I"

    private var retrofitInstance: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(
                    authApiProvider = Provider { provideAuthApi() },
                    clientId = CLIENT_ID,
                    clientSecret = CLIENT_SECRET
                ))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            retrofitInstance = retrofit
            retrofit
        }
    }

    private fun provideAuthApi(): PplAuthApi {
        return getRetrofit().create(PplAuthApi::class.java)
    }

    // Tuto metodu používejte pro získání jakékoliv API služby
    fun <T> createService(serviceClass: Class<T>): T {
        return getRetrofit().create(serviceClass)
    }
}
