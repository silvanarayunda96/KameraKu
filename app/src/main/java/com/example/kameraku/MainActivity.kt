package com.example.kameraku

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Surface
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KameraKuApp()
                }
            }
        }
    }
}

@Composable
fun KameraKuApp() {
    var hasCameraPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Izin kamera diperlukan untuk menggunakan aplikasi ini", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraScreen()
    } else {
        PermissionDeniedScreen(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    }
}

@Composable
fun PermissionDeniedScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Izin Kamera Diperlukan",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Aplikasi memerlukan akses kamera untuk mengambil foto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRequestPermission) {
                Text("Berikan Izin")
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var lastCapturedUri by remember { mutableStateOf<Uri?>(null) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    // Setup camera
    LaunchedEffect(previewView, isFrontCamera) {
        previewView?.let { view ->
            val result = setupCamera(context, lifecycleOwner, view, isFrontCamera)
            imageCapture = result.first
            camera = result.second
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    post { previewView = this }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Flash Toggle
            IconButton(
                onClick = {
                    isFlashEnabled = !isFlashEnabled
                    camera?.cameraControl?.enableTorch(isFlashEnabled)
                },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Toggle Flash",
                    tint = Color.White
                )
            }

            // Switch Camera
            IconButton(
                onClick = {
                    isFrontCamera = !isFrontCamera
                },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Thumbnail
            lastCapturedUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Last Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                        .clickable {
                            Toast.makeText(context, "Foto tersimpan di Galeri", Toast.LENGTH_SHORT).show()
                        },
                    contentScale = ContentScale.Crop
                )
            }

            // Capture Button
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .padding(4.dp)
                    .background(
                        if (isCapturing) Color.Gray else Color.White,
                        CircleShape
                    )
                    .clickable(enabled = !isCapturing) {
                        isCapturing = true
                        scope.launch {
                            imageCapture?.let { capture ->
                                val uri = takePhoto(context, capture)
                                uri?.let {
                                    lastCapturedUri = it
                                    Toast.makeText(context, "Foto tersimpan!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            isCapturing = false
                        }
                    }
            )
        }

        // Loading indicator
        if (isCapturing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

suspend fun setupCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    isFrontCamera: Boolean
): Pair<ImageCapture, Camera> = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // Preview
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        // ImageCapture
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(previewView.display?.rotation ?: Surface.ROTATION_0)
            .build()

        // Camera Selector
        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            continuation.resume(Pair(imageCapture, camera))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, ContextCompat.getMainExecutor(context))
}

suspend fun takePhoto(context: Context, imageCapture: ImageCapture): Uri? = suspendCoroutine { continuation ->
    val name = "IMG_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KameraKu")
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                continuation.resume(output.savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                continuation.resume(null)
            }
        }
    )
}