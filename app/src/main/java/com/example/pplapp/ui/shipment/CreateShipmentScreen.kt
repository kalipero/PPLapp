package com.example.pplapp.ui.shipment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShipmentScreen(
    onBackClick: () -> Unit,
    viewModel: CreateShipmentViewModel = viewModel()
) {
    val state by viewModel.state
    val scrollState = rememberScrollState()

    val euCountries = listOf("Česká republika", "Slovensko", "Německo", "Rakousko", "Polsko", "Francie", "Itálie", "Španělsko")
    val currencies = listOf("CZK", "EUR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vytvořit zásilku") },
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Recipient Address
            Text("Adresa příjemce", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            CountryDropdown(
                label = "Země doručení",
                options = euCountries,
                selectedOption = state.country,
                onOptionSelected = { viewModel.onCountryChange(it) }
            )

            OutlinedTextField(
                value = state.customerName,
                onValueChange = { viewModel.onCustomerNameChange(it) },
                label = { Text("Název zákazníka") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.street,
                onValueChange = { viewModel.onStreetChange(it) },
                label = { Text("Ulice") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = { viewModel.onCityChange(it) },
                    label = { Text("Město") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.zipCode,
                    onValueChange = { viewModel.onZipCodeChange(it) },
                    label = { Text("PSČ") },
                    modifier = Modifier.weight(0.5f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(
                value = state.contactPerson,
                onValueChange = { viewModel.onContactPersonChange(it) },
                label = { Text("Kontaktní osoba") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Telefon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Section: Shipment Parameters
            Text("Parametry zásilky", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = state.parcelCount,
                onValueChange = { viewModel.onParcelCountChange(it) },
                label = { Text("Počet kusů balíků") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.codAmount,
                    onValueChange = { viewModel.onCodAmountChange(it) },
                    label = { Text("Dobírka") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                CurrencyDropdown(
                    label = "Měna",
                    options = currencies,
                    selectedOption = state.codCurrency,
                    onOptionSelected = { viewModel.onCodCurrencyChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.variableSymbol,
                onValueChange = { viewModel.onVariableSymbolChange(it) },
                label = { Text("Variabilní symbol") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = state.printNote,
                onValueChange = { viewModel.onPrintNoteChange(it) },
                label = { Text("Poznámka pro tisk na etiketě") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.customerReference,
                onValueChange = { viewModel.onCustomerReferenceChange(it) },
                label = { Text("Zákaznická reference") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.errorMessage != null) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            if (state.successMessage != null) {
                Column {
                    Text(state.successMessage!!, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    state.labelUrl?.let { url ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Odkaz na štítek (PDF):", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.createShipment() },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Vytvořit zásilku")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
