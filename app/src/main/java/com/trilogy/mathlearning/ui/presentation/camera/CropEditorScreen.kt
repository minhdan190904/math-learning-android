package com.trilogy.mathlearning.ui.presentation.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.trilogy.mathlearning.R
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


@Composable
    fun CropEditorScreen(
        bitmap: Bitmap,
        initialRect: CropRect,
        origin: ImageOrigin,               // CAMERA → CROP ; FILE → FIT
        onDone: (Bitmap) -> Unit,
        onCancel: () -> Unit
    ) {
        val img = remember { bitmap.asImageBitmap() }
        var rect by remember { mutableStateOf(initialRect) }
        var canvasSz by remember { mutableStateOf(IntSize.Zero) }

        val density = LocalDensity.current
        val rCorner = with(density) { 16.dp.toPx() }
        val stroke = with(density) { 3.dp.toPx() }
        val handleLen = with(density) { 18.dp.toPx() }
        val minW = with(density) { 80.dp.toPx() }
        val minH = with(density) { 80.dp.toPx() }

        // Tính khung ảnh hiển thị trong canvas + scale/offset tương ứng
        fun displayedRectAndScale(): Pair<Rect, Float> {
            val w = canvasSz.width.toFloat().coerceAtLeast(1f)
            val h = canvasSz.height.toFloat().coerceAtLeast(1f)
            val bw = bitmap.width.toFloat()
            val bh = bitmap.height.toFloat()

            val scale = when (origin) {
                is ImageOrigin.CAMERA -> max(w / bw, h / bh) // ContentScale.Crop
                is ImageOrigin.FILE   -> min(w / bw, h / bh) // ContentScale.Fit (không méo)
            }
            val sw = bw * scale
            val sh = bh * scale
            val offX = (w - sw) / 2f
            val offY = (h - sh) / 2f
            return Rect(offX, offY, offX + sw, offY + sh) to scale
        }

        fun clampToImage(r: CropRect): CropRect {
            val (imgR, _) = displayedRectAndScale()
            val l = r.left.coerceIn(imgR.left, imgR.right - minW)
            val t = r.top.coerceIn(imgR.top, imgR.bottom - minH)
            val rr = r.right.coerceIn(l + minW, imgR.right)
            val bb = r.bottom.coerceIn(t + minH, imgR.bottom)
            return CropRect(l, t, rr, bb)
        }

        val contentScale = when (origin) {
            is ImageOrigin.CAMERA -> ContentScale.Crop
            is ImageOrigin.FILE   -> ContentScale.Fit
        }

        Box(Modifier.fillMaxSize()) {
            Image(
                bitmap = img,
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { canvasSz = it.size }
            )

            // Overlay + drag
            Canvas(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .pointerInput(origin, canvasSz) {
                        var drag: DragMode? = null
                        detectDragGestures(
                            onDragStart = { start -> drag = hit(start, rect) },
                            onDragEnd = { drag = null }
                        ) { change, d ->
                            change.consume()
                            rect = when (drag) {
                                DragMode.TL -> clampToImage(rect.copy(left = rect.left + d.x, top = rect.top + d.y))
                                DragMode.TR -> clampToImage(rect.copy(right = rect.right + d.x, top = rect.top + d.y))
                                DragMode.BL -> clampToImage(rect.copy(left = rect.left + d.x, bottom = rect.bottom + d.y))
                                DragMode.BR -> clampToImage(rect.copy(right = rect.right + d.x, bottom = rect.bottom + d.y))
                                DragMode.INSIDE -> clampToImage(
                                    rect.copy(
                                        left = rect.left + d.x,
                                        top = rect.top + d.y,
                                        right = rect.right + d.x,
                                        bottom = rect.bottom + d.y
                                    )
                                )
                                null -> rect
                            }
                        }
                    }
            ) {
                // mờ toàn canvas
                drawRect(Color(0x99000000))

                // vùng đục (crop) bo góc
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(rect.left, rect.top),
                    size = Size(
                        rect.right - rect.left,
                        rect.bottom - rect.top
                    ),
                    cornerRadius = CornerRadius(rCorner),
                    blendMode = BlendMode.Clear
                )

                // “ngoặc” 4 góc
                fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                    drawLine(Color.White, Offset(x, y), Offset(x + dx * handleLen, y), strokeWidth = stroke)
                    drawLine(Color.White, Offset(x, y), Offset(x, y + dy * handleLen), strokeWidth = stroke)
                }
                corner(rect.left,  rect.top,    1f, 1f)
                corner(rect.right, rect.top,   -1f, 1f)
                corner(rect.left,  rect.bottom, 1f,-1f)
                corner(rect.right, rect.bottom,-1f,-1f)
            }

            Row(
                Modifier.align(Alignment.BottomCenter).padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(80.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_close), null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp).clickable { onCancel() }
                )
                Icon(
                    painterResource(R.drawable.ic_check), null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp).clickable {
                        // Map canvas → bitmap với đúng scale/offset của ảnh hiển thị
                        val (imgR, scale) = displayedRectAndScale()
                        val offX = imgR.left
                        val offY = imgR.top

                        val x = ((rect.left - offX) / scale).toInt().coerceIn(0, bitmap.width)
                        val y = ((rect.top  - offY) / scale).toInt().coerceIn(0, bitmap.height)
                        val cw = ((rect.right - rect.left) / scale).toInt().coerceAtLeast(1)
                        val ch = ((rect.bottom - rect.top) / scale).toInt().coerceAtLeast(1)

                        val crop = Bitmap.createBitmap(
                            bitmap,
                            x, y,
                            (x + cw).coerceAtMost(bitmap.width) - x,
                            (y + ch).coerceAtMost(bitmap.height) - y
                        )
                        onDone(crop)
                    }
                )
            }
        }
    }

    private enum class DragMode { TL, TR, BL, BR, INSIDE }

    private fun hit(p: Offset, r: CropRect, range: Float = 48f): DragMode? =
        when {
            (p.x - r.left).absoluteValue  < range && (p.y - r.top).absoluteValue    < range -> DragMode.TL
            (p.x - r.right).absoluteValue < range && (p.y - r.top).absoluteValue    < range -> DragMode.TR
            (p.x - r.left).absoluteValue  < range && (p.y - r.bottom).absoluteValue < range -> DragMode.BL
            (p.x - r.right).absoluteValue < range && (p.y - r.bottom).absoluteValue < range -> DragMode.BR
            p.x in r.left..r.right && p.y in r.top..r.bottom -> DragMode.INSIDE
            else -> null
        }
