@file:OptIn(ExperimentalLayoutApi::class)

package com.yonasoft.jadedictionary.features.ocr.presentation.components

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.yonasoft.jadedictionary.R
import com.yonasoft.jadedictionary.core.constants.CustomColor
import java.io.ByteArrayInputStream
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onOCRCompleted: (Bitmap) -> Unit,
    recognizedText: List<String> = emptyList(),
    onTextSelected: (String) -> Unit = {},
    isRecognizing: Boolean = false
) {
    val context = LocalContext.current
    androidx.lifecycle.compose.LocalLifecycleOwner.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    rememberCoroutineScope()

    // Camera states
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    // Request camera permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri ->
            try {
                // Get bitmap from Uri
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Handle rotation for gallery images
                val rotatedBitmap = getCorrectlyOrientedBitmap(context, imageUri, bitmap)

                capturedImage = rotatedBitmap
                onOCRCompleted(rotatedBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Request camera permission on launch
    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Hide bottom sheet when not visible
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            sheetState.hide()
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF050505), // Darker background for better contrast
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.6f)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OCR Scanner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1A1A1A))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Camera preview or captured image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp)) // Larger corner radius
                        .background(Color(0xFF121212)) // Slightly lighter than background
                ) {
                    if (capturedImage != null) {
                        // Show captured image
                        Image(
                            bitmap = capturedImage!!.asImageBitmap(),
                            contentDescription = "Captured image",
                            modifier = Modifier.fillMaxSize()
                        )

                        // Retake button
                        IconButton(
                            onClick = { capturedImage = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Retake",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else if (cameraPermissionGranted) {
                        // Show camera preview
                        CameraPreview(
                            onCameraReady = { imageCapture = it }
                        )

                        // Camera controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Gallery button
                            IconButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_insert_photo_24),
                                    contentDescription = "Gallery",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Capture button with gradient background
                            IconButton(
                                onClick = {
                                    imageCapture?.let { capture ->
                                        captureImage(
                                            imageCapture = capture,
                                            executor = ContextCompat.getMainExecutor(context),
                                            onImageCaptured = { bitmap ->
                                                capturedImage = bitmap
                                                onOCRCompleted(bitmap)
                                            },
                                            onError = { exception ->
                                                exception.printStackTrace()
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                CustomColor.GREEN01.color.copy(alpha = 0.8f),
                                                CustomColor.GREEN01.color.copy(alpha = 0.4f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_camera_alt_24),
                                    contentDescription = "Take Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    } else {
                        // Camera permission not granted
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Camera permission is required",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp,
                                letterSpacing = 0.3.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    "Grant Permission",
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1A1A1A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    "Select from Gallery",
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Recognized text results
                if (isRecognizing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = CustomColor.GREEN01.color,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp),
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Recognizing text...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            letterSpacing = 0.3.sp
                        )
                    }
                } else if (recognizedText.isNotEmpty()) {
                    Text(
                        text = "Recognized Text",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color.White,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 4
                    ) {
                        recognizedText.forEach { text ->
                            SuggestionChip(
                                onClick = { onTextSelected(text) },
                                label = {
                                    Text(
                                        text = text,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = CustomColor.GREEN01.color.copy(alpha = 0.2f),
                                    labelColor = Color.White
                                ),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else if (capturedImage != null) {
                    // No text recognized yet, but image is captured
                    Text(
                        text = "No text recognized. Try adjusting the image or taking another photo.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    )
                }

                // Bottom margin for better UX
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier.fillMaxSize(),
    onCameraReady: (ImageCapture) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Set up the preview use case
                val preview = Preview.Builder().build()
                preview.surfaceProvider = previewView.surfaceProvider

                // Set up image capture use case
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                // Select back camera as default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind any bound use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                    // Notify the caller that the camera is ready
                    onCameraReady(imageCapture)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}


// Replace your current captureImage function with this one
private fun captureImage(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)

                // Decode the image
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                // Get the image rotation from EXIF data
                val rotatedBitmap = getRotatedBitmap(bitmap, bytes)

                onImageCaptured(rotatedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

// Add this new function to correctly rotate the bitmap
private fun getRotatedBitmap(bitmap: Bitmap, imageBytes: ByteArray): Bitmap {
    // Read EXIF orientation from the image data
    val inputStream = ByteArrayInputStream(imageBytes)
    val exif = try {
        ExifInterface(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        return bitmap
    }

    // Get the orientation from EXIF data
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    // Calculate rotation angle based on EXIF orientation
    val rotationAngle = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    // If no rotation needed, return the original bitmap
    if (rotationAngle == 0f) {
        return bitmap
    }

    // Create a matrix for the rotation
    val matrix = Matrix()
    matrix.postRotate(rotationAngle)

    // Create a new bitmap with the correct orientation
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}


// Add this function to handle gallery image rotation
private fun getCorrectlyOrientedBitmap(context: android.content.Context, uri: Uri, bitmap: Bitmap): Bitmap {
    // Get input stream from URI
    val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap

    // Read EXIF data
    val exif = try {
        ExifInterface(inputStream)
    } catch (e: Exception) {
        inputStream.close()
        return bitmap
    }

    inputStream.close()

    // Get orientation and rotate if needed
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val rotationAngle = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    if (rotationAngle == 0f) {
        return bitmap
    }

    val matrix = Matrix()
    matrix.postRotate(rotationAngle)

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}