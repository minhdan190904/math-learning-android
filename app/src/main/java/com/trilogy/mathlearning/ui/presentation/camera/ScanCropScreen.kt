@file:Suppress("DEPRECATION")

package com.trilogy.mathlearning.ui.presentation.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.view.Surface
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.math.min

@Composable
fun ScanCropScreen(
    modifier: Modifier = Modifier,
    onCaptured: (Bitmap, CropRect, ImageOrigin) -> Unit
) {
    val ctx = LocalContext.current

    /* ───── permission (camera) ───── */
    var hasCam by remember { mutableStateOf(false) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasCam = it }
    LaunchedEffect(Unit) { permLauncher.launch(Manifest.permission.CAMERA) }

    /* ───── Photo Picker (gallery/file) ───── */
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bmp = decodeBitmapFromUri(ctx, uri, maxSide = 4096)
            if (bmp != null) {
                val r = lastOverlayRect
                onCaptured(
                    bmp,
                    CropRect(r.left, r.top, r.right, r.bottom),
                    ImageOrigin.FILE
                )
            }
        }
    }

    /* ───── Camera state ───── */
    val owner = LocalLifecycleOwner.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var torch by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    fun bindCamera(pv: PreviewView) {
        val providerFut = ProcessCameraProvider.getInstance(ctx)
        providerFut.addListener({
            val provider = providerFut.get()

            val previewUC = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also { it.setSurfaceProvider(pv.surfaceProvider) }

            val capUC = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(pv.display?.rotation ?: Surface.ROTATION_0)
                .build()

            provider.unbindAll()
            camera = provider.bindToLifecycle(
                owner, CameraSelector.DEFAULT_BACK_CAMERA, previewUC, capUC
            )
            imageCapture = capUC
        }, ContextCompat.getMainExecutor(ctx))
    }

    Box(modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { c ->
                PreviewView(c).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Preview camera luôn fill (CROP)
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                    if (hasCam) bindCamera(this)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { canvasSize = it.size },
            update = { if (hasCam) bindCamera(it) }
        )

        // ─── Khung crop gợi ý (overlay) ───
        val vw = canvasSize.width.coerceAtLeast(1).toFloat()
        val vh = canvasSize.height.coerceAtLeast(1).toFloat()
        val rect = Rect(
            vw * 0.075f,
            vh * 0.25f,
            vw * 0.925f,
            vh * 0.25f + vh * 0.28f
        )
        lastOverlayRect = rect

        Canvas(Modifier.fillMaxSize()) {
            drawPath(
                Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect,
                            CornerRadius(16.dp.toPx())
                        )
                    )
                },
                Color.Transparent,
                blendMode = BlendMode.Clear
            )
            drawRoundRect(
                Color.White,
                Offset(rect.left, rect.top),
                rect.size,
                CornerRadius(16.dp.toPx()),
                style = Stroke(1.dp.toPx())
            )
        }

        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── GALLERY (FILE → FIT ở màn crop) ───
            Icon(
                Icons.Default.Image,
                contentDescription = "Gallery",
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )

            // ─── SHUTTER (CAMERA → CROP ở màn crop) ───
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color(0xFF2962FF), CircleShape)
                    .clickable(enabled = hasCam && imageCapture != null) {
                        val ic = imageCapture ?: return@clickable
                        ic.takePicture(
                            ContextCompat.getMainExecutor(ctx),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(img: ImageProxy) {
                                    val bmp = imageProxyToBitmap(img)
                                    img.close()
                                    onCaptured(
                                        bmp,
                                        CropRect(rect.left, rect.top, rect.right, rect.bottom),
                                        ImageOrigin.CAMERA
                                    )
                                }

                                override fun onError(exc: ImageCaptureException) {
                                    exc.printStackTrace()
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShutterSpeed,
                    contentDescription = "Capture",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // ─── TORCH ───
            Icon(
                Icons.Default.FlashOn,
                contentDescription = "Torch",
                tint = if (torch) Color.Yellow else Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(enabled = camera != null) {
                        torch = !torch
                        camera?.cameraControl?.enableTorch(torch)
                    }
            )
        }
    }
}

/* ─────────── helpers: ImageProxy → Bitmap (JPEG) + xoay theo rotationDegrees ─────────── */
private fun imageProxyToBitmap(img: ImageProxy): Bitmap {
    val buffer = img.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    var bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rot = img.imageInfo.rotationDegrees
    if (rot != 0) {
        val m = Matrix().apply { postRotate(rot.toFloat()) }
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
    }
    return bmp
}

/* ─────────── helpers: decode uri → bitmap (safe) + sửa EXIF ─────────── */
private fun decodeBitmapFromUri(
    context: Context,
    uri: Uri,
    maxSide: Int = 4096
): Bitmap? {
    val optsBounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, optsBounds)
    }

    val (srcW, srcH) = optsBounds.outWidth to optsBounds.outHeight
    if (srcW <= 0 || srcH <= 0) {
        context.contentResolver.openInputStream(uri)?.use {
            return BitmapFactory.decodeStream(it)
        }
    }

    val scale = min(maxSide.toFloat() / srcW, maxSide.toFloat() / srcH)
    val targetW = (srcW * scale).toInt().coerceAtLeast(1)
    val targetH = (srcH * scale).toInt().coerceAtLeast(1)

    var sample = 1
    while ((srcW / (sample * 2)) >= targetW && (srcH / (sample * 2)) >= targetH) {
        sample *= 2
    }

    val opts = BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    var bmp = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, opts)
    } ?: return null

    val rotated = applyExifOrientation(context, uri, bmp)
    if (rotated != bmp) bmp.recycle()
    return rotated
}

private fun applyExifOrientation(context: Context, uri: Uri, bmp: Bitmap): Bitmap {
    return try {
        val exif = context.contentResolver.openInputStream(uri)?.use { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90  -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL   -> m.preScale(1f, -1f)
            else -> { /* no-op */ }
        }
        if (!m.isIdentity) {
            Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
        } else bmp
    } catch (_: Exception) {
        bmp
    }
}

/* Lưu khung overlay hiện tại để tái sử dụng khi pick ảnh từ gallery */
private var lastOverlayRect: Rect = Rect(0f, 0f, 0f, 0f)
