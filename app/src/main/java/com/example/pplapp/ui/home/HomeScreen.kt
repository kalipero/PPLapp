package com.example.pplapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateShipmentClick: () -> Unit = {},
    onTrackShipmentClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val shipments by viewModel.createdShipments.collectAsState()

    LaunchedEffect(Unit) {
        android.util.Log.d("HomeScreen", "Started. Current shipments in storage: ${shipments.size}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PPL Moje Aplikace") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeButton(
                text = "Vytvořit zásilku",
                icon = Icons.Default.Add,
                onClick = onCreateShipmentClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeButton(
                text = "Sledovat zásilku",
                icon = Icons.Default.Search,
                onClick = onTrackShipmentClick
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Tlačítko pro obnovu tokenu (menší verze)
            OutlinedButton(
                onClick = { viewModel.refreshToken() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is HomeUiState.Loading
            ) {
                if (uiState is HomeUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obnovit Token")
                }
            }

            if (uiState is HomeUiState.Success || uiState is HomeUiState.Error) {
                Text(
                    text = (uiState as? HomeUiState.Success)?.message ?: (uiState as? HomeUiState.Error)?.message ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (uiState is HomeUiState.Success) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sekce vytvořených zásilek
            Text(
                text = "Vytvořené zásilky",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            if (shipments.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Zatím žádné vytvořené zásilky", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(shipments) { shipment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = shipment.shipmentNumber,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(shipment.createdAt)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = shipment.recipientName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
