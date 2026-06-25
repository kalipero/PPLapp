package com.example.pplapp.ui.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pplapp.data.model.TrackingEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onBackClick: () -> Unit,
    viewModel: TrackingViewModel = viewModel()
) {
    var shipmentNumber by remember { mutableStateOf("") }
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sledovat zásilku") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = shipmentNumber,
                    onValueChange = { shipmentNumber = it },
                    label = { Text("Číslo zásilky") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.searchShipment(shipmentNumber) },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Hledat")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is TrackingUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is TrackingUiState.Error -> {
                    Text(
                        text = "Chyba: ${state.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                is TrackingUiState.Success -> {
                    // Zobrazení syrového JSONu pod vyhledáváním
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                text = "Odpověď ze serveru (JSON):",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.rawJson,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }

                    val shipment = state.shipments.firstOrNull()
                    if (shipment != null) {
                        Text(
                            text = "Stav: ${shipment.status ?: "Neznámý"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Historie událostí:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(shipment.events) { event ->
                                TrackingEventItem(event)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    } else {
                        Text(text = "Žádná data pro toto číslo zásilky neexistují.")
                    }
                }
                else -> {
                    Text(text = "Zadejte číslo balíku pro vyhledání.")
                }
            }
        }
    }
}

@Composable
fun TrackingEventItem(event: TrackingEvent) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = event.timestamp, color = Color.Gray, fontSize = 12.sp)
            event.location?.let {
                Text(text = it, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = event.description, style = MaterialTheme.typography.bodyLarge)
    }
}
