package com.example.studentattandance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.studentattandance.ui.theme.StudentAttandanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentAttandanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var showScanner by remember { mutableStateOf(false) }
    var scannedQRData by remember { mutableStateOf<String?>(null) }
    
    if (showScanner) {
        // Show QR Scanner
        QRScannerScreen(
            onQrCodeScanned = { qrData ->
                scannedQRData = qrData
                showScanner = false
                // TODO: Process the scanned QR code (e.g., mark attendance)
                println("Scanned QR Code: $qrData")
            },
            onBackPressed = {
                showScanner = false
            }
        )
    } else {
        when (currentScreen) {
            AppScreen.DASHBOARD -> {
                StudentDashboardScreen(
                    userName = "yehia",
                    attendanceMissedCount = 0,
                    onScanClick = {
                        showScanner = true
                    },
                    onSessionsClick = {
                        currentScreen = AppScreen.SESSIONS
                    }
                )
            }
            AppScreen.SESSIONS -> {
                SessionsScreen(
                    userAvatarUrl = null, // TODO: Add user avatar URL
                    onNotificationClick = {
                        // TODO: Handle notification click
                    },
                    onSessionClick = { session ->
                        // TODO: Navigate to session details
                        println("Session clicked: ${session.title}")
                    },
                    onCheckInClick = { session ->
                        // Open QR scanner for check-in
                        showScanner = true
                    }
                )
            }
        }
    }
}

enum class AppScreen {
    DASHBOARD,
    SESSIONS
}