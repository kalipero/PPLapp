package com.example.pplapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pplapp.ui.home.HomeScreen
import com.example.pplapp.ui.shipment.CreateShipmentScreen
import com.example.pplapp.ui.theme.PPLappTheme
import com.example.pplapp.ui.tracking.TrackingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PPLappTheme {
                var currentScreen by remember { mutableStateOf("home") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onCreateShipmentClick = { currentScreen = "shipment" },
                            onTrackShipmentClick = { currentScreen = "tracking" }
                        )
                        "tracking" -> TrackingScreen(
                            onBackClick = { currentScreen = "home" }
                        )
                        "shipment" -> CreateShipmentScreen(
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PPLappTheme {
        Greeting("Android")
    }
}