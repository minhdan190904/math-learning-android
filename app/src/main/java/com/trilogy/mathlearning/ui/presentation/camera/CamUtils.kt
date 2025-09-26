@file:Suppress("DEPRECATION")

package com.trilogy.mathlearning.ui.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.media.Image
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.core.graphics.createBitmap
import java.io.File

/* ───── public API ───── */

@OptIn(ExperimentalGetImage::class)
fun imageProxyToBitmap(ctx: Context, proxy: ImageProxy): Bitmap {
    val bmp = when (proxy.format) {
        ImageFormat.JPEG -> {
            // JPEG: 1 plane → decode thẳng
            val buf = proxy.planes[0].buffer
            val bytes = ByteArray(buf.remaining())
            buf.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        ImageFormat.YUV_420_888 -> {
            // YUV: dùng converter
            val out = createBitmap(proxy.width, proxy.height)
            YuvToRgbConverter(ctx).yuvToRgb(proxy, out)
            out
        }
        else -> {
            // Fallback (một số máy vẫn trả JPEG)
            val buf = proxy.planes[0].buffer
            val bytes = ByteArray(buf.remaining())
            buf.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    val rot = proxy.imageInfo.rotationDegrees
    return if (rot == 0) bmp
    else Bitmap.createBitmap(
        bmp, 0, 0, bmp.width, bmp.height,
        Matrix().apply { postRotate(rot.toFloat()) }, true
    )
}

/* ───── impl cho YUV → RGB (NV21 + RenderScript) ───── */

@Suppress("DEPRECATION")
class YuvToRgbConverter(ctx: Context) {

    private val rs     = RenderScript.create(ctx)
    private val script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

    private var yuvAlloc: Allocation? = null
    private var rgbAlloc: Allocation? = null
    private var nv21Buf: ByteArray = ByteArray(0)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    fun yuvToRgb(proxy: ImageProxy, out: Bitmap) = yuvToRgb(proxy.image!!, out)

    fun yuvToRgb(img: Image, out: Bitmap) {
        require(img.format == ImageFormat.YUV_420_888) { "Expect YUV_420_888" }

        val w = img.width
        val h = img.height
        val yPlane = img.planes[0]
        val uPlane = img.planes[1]
        val vPlane = img.planes[2]

        val ySize = w * h
        val uvSize = w * h / 2
        val need = ySize + uvSize
        if (nv21Buf.size != need) nv21Buf = ByteArray(need)

        // Copy Y (tôn trọng rowStride/pixelStride)
        var pos = 0
        val yRowStride = yPlane.rowStride
        val yPixStride = yPlane.pixelStride
        val yBuf = yPlane.buffer
        yBuf.rewind()
        for (row in 0 until h) {
            val base = row * yRowStride
            if (yPixStride == 1) {
                yBuf.position(base)
                yBuf.get(nv21Buf, pos, w)
                pos += w
            } else {
                for (col in 0 until w) {
                    nv21Buf[pos++] = yBuf.get(base + col * yPixStride)
                }
            }
        }

        // Copy chroma VU (NV21) theo stride
        val uRowStride = uPlane.rowStride
        val vRowStride = vPlane.rowStride
        val uPixStride = uPlane.pixelStride
        val vPixStride = vPlane.pixelStride
        val uBuf = uPlane.buffer.also { it.rewind() }
        val vBuf = vPlane.buffer.also { it.rewind() }

        val chromaH = h / 2
        val chromaW = w / 2
        for (row in 0 until chromaH) {
            val uBase = row * uRowStride
            val vBase = row * vRowStride
            for (col in 0 until chromaW) {
                val u = uBuf.get(uBase + col * uPixStride)
                val v = vBuf.get(vBase + col * vPixStride)
                nv21Buf[pos++] = v
                nv21Buf[pos++] = u
            }
        }

        if (yuvAlloc == null || yuvAlloc!!.bytesSize != need)
            yuvAlloc = Allocation.createSized(rs, Element.U8(rs), need)
        if (rgbAlloc == null || rgbAlloc!!.type.x != out.width || rgbAlloc!!.type.y != out.height)
            rgbAlloc = Allocation.createFromBitmap(rs, out)

        yuvAlloc!!.copyFrom(nv21Buf)
        script.setInput(yuvAlloc)
        script.forEach(rgbAlloc)
        rgbAlloc!!.copyTo(out)
    }
}

fun saveBitmapToCache(context: Context, bmp: Bitmap): String {
    val file = File(context.cacheDir, "edited_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    // Nếu bạn đã cấu hình FileProvider, nên trả về content://
    // Ở đây demo trả file:// đơn giản:
    return file.toURI().toString()
}

