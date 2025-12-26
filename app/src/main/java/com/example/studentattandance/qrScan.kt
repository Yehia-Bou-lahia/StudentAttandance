package com.example.studentattandance

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.studentattandance.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * QR Code Scanner Screen
 * 
 * Features:
 * - Camera preview with ML Kit barcode scanning
 * - Custom UI with blue corner brackets matching the design
 * - Animated scanning line
 * - Flash toggle
 * - Scan result handling
 * 
 * @param onQrCodeScanned Callback when QR code is successfully scanned
 * @param onBackPressed Callback when back button is pressed
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    onQrCodeScanned: (String) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var flashEnabled by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                // Camera preview
                CameraPreview(
                    onQrCodeScanned = { qrData ->
                        scannedData = qrData
                        showSuccessDialog = true
                    },
                    flashEnabled = flashEnabled
                )
                
                // Overlay UI
                QRScannerOverlay(
                    onBackPressed = onBackPressed,
                    flashEnabled = flashEnabled,
                    onFlashToggle = { flashEnabled = !flashEnabled }
                )
            }
            cameraPermissionState.status.shouldShowRationale -> {
                // Permission rationale
                PermissionRationale(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onBackPressed = onBackPressed
                )
            }
            else -> {
                // Permission denied
                PermissionDenied(onBackPressed = onBackPressed)
            }
        }
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onQrCodeScanned(scannedData)
                onBackPressed()
            },
            containerColor = CardNavy,
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            colorFilter = ColorFilter.tint(SuccessGreen)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Success!",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = "You have registered your attendance for this course.",
                    color = TextPrimary.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onQrCodeScanned(scannedData)
                        onBackPressed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Done",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun CameraPreview(
    onQrCodeScanned: (String) -> Unit,
    flashEnabled: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasScanned by remember { mutableStateOf(false) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val barcodeScanner = BarcodeScanning.getClient()

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !hasScanned) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    when (barcode.valueType) {
                                        Barcode.TYPE_TEXT,
                                        Barcode.TYPE_URL,
                                        Barcode.TYPE_CONTACT_INFO,
                                        Barcode.TYPE_EMAIL,
                                        Barcode.TYPE_PHONE,
                                        Barcode.TYPE_SMS,
                                        Barcode.TYPE_WIFI,
                                        Barcode.TYPE_GEO,
                                        Barcode.TYPE_CALENDAR_EVENT,
                                        Barcode.TYPE_DRIVER_LICENSE,
                                        Barcode.TYPE_PRODUCT -> {
                                            barcode.rawValue?.let { value ->
                                                if (!hasScanned) {
                                                    hasScanned = true
                                                    onQrCodeScanned(value)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    
                    // Enable flash if requested
                    if (flashEnabled && camera.cameraInfo.hasFlashUnit()) {
                        camera.cameraControl.enableTorch(true)
                    } else {
                        camera.cameraControl.enableTorch(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, executor)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun QRScannerOverlay(
    onBackPressed: () -> Unit,
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top bar with back button and flash toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title
            Text(
                text = "Scan QR Code",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Flash toggle
            IconButton(
                onClick = onFlashToggle,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (flashEnabled) AccentBlue else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Scanning frame with corner brackets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 120.dp, horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            ScanningFrame()
        }

        // Bottom instruction text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.qr),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(AccentBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Align QR code within the frame",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ScanningFrame() {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse  // Up and down motion
        ),
        label = "scanLine"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)  // Rectangular shape
    ) {
        // Scan frame icon from drawable
        Image(
            painter = painterResource(id = R.drawable.scan),
            contentDescription = "Scan Frame",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(AccentBlue)
        )
        
        // Animated scanning line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanLineY = size.height * scanLinePosition
            val gradient = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    AccentBlue.copy(alpha = 0.8f),
                    Color.Transparent
                ),
                startY = scanLineY - 30.dp.toPx(),
                endY = scanLineY + 30.dp.toPx()
            )
            
            drawRect(
                brush = gradient,
                topLeft = Offset(0f, scanLineY - 1.dp.toPx()),
                size = ComposeSize(size.width, 2.dp.toPx())
            )
        }
    }
}

@Composable
fun PermissionRationale(
    onRequestPermission: () -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(AccentBlue)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Camera Permission Required",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "We need camera access to scan QR codes for attendance tracking.",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Grant Permission",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBackPressed) {
                Text(
                    text = "Go Back",
                    color = TextPrimary.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun PermissionDenied(onBackPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wrong),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(MutedRed)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Camera Access Denied",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Please enable camera permission in your device settings to use the QR scanner.",
                color = TextPrimary.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Go Back",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
// doesn't finish yet still need testing using real data with an api
