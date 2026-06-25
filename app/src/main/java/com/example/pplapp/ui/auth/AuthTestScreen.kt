package com.example.pplapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pplapp.data.api.NetworkModule
import com.example.pplapp.data.api.PplAuthApi
import com.example.pplapp.data.auth.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthTestScreen() {
    val scope = rememberCoroutineScope()
    var tokenState by remember { mutableStateOf("Token zatím nebyl vyžádán") }
    var isLoading by remember { mutableStateOf(false) }
    var errorState by remember { mutableStateOf<String?>(null) }

    val authApi = remember { NetworkModule.createService(PplAuthApi::class.java) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PPL Auth Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorState = null
                    try {
                        // Vynutíme smazání starého tokenu pro test
                        TokenManager.clearToken()
                        
                        // Voláme přímo AuthApi pro získání tokenu
                        // Poznámka: V reálném provozu to za nás dělá Interceptor
                        val response = withContext(Dispatchers.IO) {
                            authApi.getToken(
                                clientId = "EB2050937",
                                clientSecret = "KWC3WwInmpe0LR5Zgpvd971KxLscWO8I"
                            ).execute()
                        }

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                TokenManager.saveToken(body)
                                tokenState = "Token úspěšně získán:\n\n${body.accessToken}"
                            } else {
                                errorState = "Prázdná odpověď od serveru"
                            }
                        } else {
                            errorState = "Chyba ${response.code()}: ${response.errorBody()?.string()}"
                        }
                    } catch (e: Exception) {
                        errorState = "Výjimka: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Získat nový Token")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (errorState != null) {
            Text(
                text = errorState!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = tokenState,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Platnost tokenu: ${if (TokenManager.isTokenValid()) "PLATNÝ" else "NEPLATNÝ/CHYBÍ"}",
            style = MaterialTheme.typography.labelMedium,
            color = if (TokenManager.isTokenValid()) Color(0xFF4CAF50) else Color.Red
        )
    }
}
