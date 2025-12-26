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
        // Show Dashboard with scan button connected
        StudentDashboardScreen(
            userName = "yehia",
            attendanceMissedCount = 0, // Will automatically show SUCCESS state
            onScanClick = {
                showScanner = true
            }
        )
    }
}