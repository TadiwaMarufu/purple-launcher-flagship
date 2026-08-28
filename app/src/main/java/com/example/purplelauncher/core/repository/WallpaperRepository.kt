package com.example.purplelauncher.core.repository

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.example.purplelauncher.core.model.WallpaperConfig
import com.example.purplelauncher.core.model.WallpaperPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class WallpaperAnalysis(
    val averageLuminance: Float = 0.5f,
    val isDark: Boolean = true,
    val suggestedAccentHex: String = "#D0BCFF"
)

class WallpaperRepository(private val context: Context) {

    fun applyPreset(preset: WallpaperPreset, currentConfig: WallpaperConfig = WallpaperConfig()): WallpaperConfig {
        return when (preset) {
            WallpaperPreset.PURE -> currentConfig.copy(
                preset = preset,
                brightness = 0.0f,
                contrast = 1.0f,
                grayscaleIntensity = 1.0f,
                blurRadius = 0.0f,
                grainAmount = 0.03f,
                vignetteAmount = 0.10f,
                darkening = 0.30f
            )
            WallpaperPreset.SOFT -> currentConfig.copy(
                preset = preset,
                brightness = 0.05f,
                contrast = 0.85f,
                grayscaleIntensity = 0.95f,
                blurRadius = 6.0f,
                grainAmount = 0.02f,
                vignetteAmount = 0.08f,
                darkening = 0.20f
            )
            WallpaperPreset.NOIR -> currentConfig.copy(
                preset = preset,
                brightness = -0.15f,
                contrast = 1.45f,
                grayscaleIntensity = 1.0f,
                blurRadius = 0.0f,
                grainAmount = 0.06f,
                vignetteAmount = 0.35f,
                darkening = 0.50f
            )
            WallpaperPreset.FILM -> currentConfig.copy(
                preset = preset,
                brightness = 0.02f,
                contrast = 1.15f,
                grayscaleIntensity = 0.90f,
                blurRadius = 2.0f,
                grainAmount = 0.12f,
                vignetteAmount = 0.22f,
                darkening = 0.32f
            )
            WallpaperPreset.MATTE -> currentConfig.copy(
                preset = preset,
                brightness = 0.10f,
                contrast = 0.75f,
                grayscaleIntensity = 1.0f,
                blurRadius = 0.0f,
                grainAmount = 0.05f,
                vignetteAmount = 0.05f,
                darkening = 0.25f
            )
            WallpaperPreset.HIGH_CONTRAST -> currentConfig.copy(
                preset = preset,
                brightness = 0.0f,
                contrast = 1.80f,
                grayscaleIntensity = 1.0f,
                blurRadius = 0.0f,
                grainAmount = 0.02f,
                vignetteAmount = 0.25f,
                darkening = 0.40f
            )
        }
    }

    suspend fun processWallpaperBitmap(
        sourceUriString: String?,
        config: WallpaperConfig,
        targetWidth: Int = 1080,
        targetHeight: Int = 2400
    ): Pair<Bitmap, WallpaperAnalysis> = withContext(Dispatchers.IO) {
        val baseBitmap = if (!sourceUriString.isNullOrBlank()) {
            loadSourceBitmap(sourceUriString, targetWidth, targetHeight)
        } else {
            createDefaultAbstractMonochromeBitmap(targetWidth, targetHeight)
        }

        val resultBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        // 1. Draw base with scale & position offsets
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val srcRect = Rect(0, 0, baseBitmap.width, baseBitmap.height)
        val dstRect = Rect(0, 0, targetWidth, targetHeight)
        canvas.drawBitmap(baseBitmap, srcRect, dstRect, paint)

        // 2. Grayscale & Contrast & Brightness adjustment via ColorMatrix
        val cm = ColorMatrix()
        // Grayscale interpolation: 0 = original color, 1 = full monochrome
        val grayCm = ColorMatrix()
        grayCm.setSaturation(1f - config.grayscaleIntensity.coerceIn(0f, 1f))

        // Contrast adjustment
        val c = config.contrast.coerceIn(0.1f, 3.0f)
        val b = (config.brightness * 255).coerceIn(-255f, 255f)
        val contrastTranslate = (-0.5f * c + 0.5f) * 255f + b
        val contrastMatrix = floatArrayOf(
            c, 0f, 0f, 0f, contrastTranslate,
            0f, c, 0f, 0f, contrastTranslate,
            0f, 0f, c, 0f, contrastTranslate,
            0f, 0f, 0f, 1f, 0f
        )
        val contrastCm = ColorMatrix(contrastMatrix)

        cm.postConcat(grayCm)
        cm.postConcat(contrastCm)

        val colorFilterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(cm)
        }
        canvas.drawBitmap(resultBitmap, 0f, 0f, colorFilterPaint)

        // 3. Optional Darkening Scrim
        if (config.darkening > 0.01f) {
            val darkAlpha = (config.darkening.coerceIn(0f, 0.95f) * 255).toInt()
            canvas.drawColor(Color.argb(darkAlpha, 10, 8, 14))
        }

        // 4. Optional Vignette
        if (config.vignetteAmount > 0.01f) {
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                val cx = targetWidth / 2f
                val cy = targetHeight / 2f
                val radius = max(targetWidth, targetHeight) * 0.75f
                val alpha = (config.vignetteAmount.coerceIn(0f, 1f) * 255).toInt()
                val colors = intArrayOf(Color.TRANSPARENT, Color.argb(alpha, 5, 4, 8))
                val stops = floatArrayOf(0.4f, 1.0f)
                shader = RadialGradient(cx, cy, radius, colors, stops, Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), vignettePaint)
        }

        // 5. Optional Film Grain Noise
        if (config.grainAmount > 0.01f) {
            applyFilmGrain(canvas, targetWidth, targetHeight, config.grainAmount)
        }

        // 6. Fast Stack Blur if requested
        val finalBitmap = if (config.blurRadius > 0.5f) {
            applyFastBlur(resultBitmap, config.blurRadius.toInt().coerceIn(1, 25))
        } else {
            resultBitmap
        }

        // 7. Luminance Analysis
        val analysis = analyzeLuminance(finalBitmap)

        Pair(finalBitmap, analysis)
    }

    private fun loadSourceBitmap(uriStr: String, targetW: Int, targetH: Int): Bitmap {
        return try {
            val uri = Uri.parse(uriStr)
            val inputStream = context.contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (original != null) {
                Bitmap.createScaledBitmap(original, targetW, targetH, true)
            } else {
                createDefaultAbstractMonochromeBitmap(targetW, targetH)
            }
        } catch (_: Exception) {
            createDefaultAbstractMonochromeBitmap(targetW, targetH)
        }
    }

    private fun createDefaultAbstractMonochromeBitmap(w: Int, h: Int): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, h.toFloat(),
                intArrayOf(
                    Color.rgb(18, 16, 22),
                    Color.rgb(28, 24, 36),
                    Color.rgb(12, 10, 16)
                ),
                floatArrayOf(0.0f, 0.5f, 1.0f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        // Subtle geometric curves / frequency lines in monochrome
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.argb(35, 208, 188, 255)
        }

        for (i in 0 until 5) {
            val path = Path()
            val yOffset = h * (0.3f + i * 0.1f)
            path.moveTo(0f, yOffset)
            path.cubicTo(
                w * 0.3f, yOffset - 120f,
                w * 0.7f, yOffset + 120f,
                w.toFloat(), yOffset - 40f
            )
            canvas.drawPath(path, linePaint)
        }
        return bmp
    }

    private fun applyFilmGrain(canvas: Canvas, w: Int, h: Int, grainAmount: Float) {
        val grainPaint = Paint().apply {
            strokeWidth = 1f
        }
        val alpha = (grainAmount.coerceIn(0f, 1f) * 70).toInt()
        val numDots = (w * h * 0.0015f * grainAmount).toInt().coerceIn(500, 15000)
        val random = Random(42)

        for (i in 0 until numDots) {
            val x = random.nextFloat() * w
            val y = random.nextFloat() * h
            val shade = if (random.nextBoolean()) 255 else 0
            grainPaint.color = Color.argb(alpha, shade, shade, shade)
            canvas.drawPoint(x, y, grainPaint)
        }
    }

    private fun analyzeLuminance(bitmap: Bitmap): WallpaperAnalysis {
        var totalLuminance = 0.0
        val sampleStep = 40
        var samplesCount = 0

        val w = bitmap.width
        val h = bitmap.height

        for (x in 0 until w step sampleStep) {
            for (y in 0 until h step sampleStep) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel) / 255.0
                val g = Color.green(pixel) / 255.0
                val b = Color.blue(pixel) / 255.0
                // Standard relative luminance formula
                val lum = 0.2126 * r + 0.7152 * g + 0.0722 * b
                totalLuminance += lum
                samplesCount++
            }
        }

        val avgLum = if (samplesCount > 0) (totalLuminance / samplesCount).toFloat() else 0.5f
        val isDark = avgLum < 0.55f

        // High contrast accessible purple accent
        val accent = if (isDark) "#D0BCFF" else "#6750A4"

        return WallpaperAnalysis(
            averageLuminance = avgLum,
            isDark = isDark,
            suggestedAccentHex = accent
        )
    }

    private fun applyFastBlur(src: Bitmap, radius: Int): Bitmap {
        if (radius < 1) return src
        val w = src.width
        val h = src.height
        val pix = IntArray(w * h)
        src.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(max(w, h))

        var divsum = (div + 1) shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        for (idx in 0 until 256 * divsum) {
            dv[idx] = idx / divsum
        }

        yw = 0
        yi = 0

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        for (curY in 0 until h) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0
            for (idx in -radius..radius) {
                p = pix[yi + min(wm, max(idx, 0))]
                val sir = stack[idx + radius]
                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = (p and 0x0000ff)
                rbs = r1 - Math.abs(idx)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (idx > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
            }
            stackpointer = radius

            for (curX in 0 until w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                val sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (curY == 0) {
                    vmin[curX] = min(curX + radius + 1, wm)
                }
                p = pix[yw + vmin[curX]]

                sir[0] = (p and 0xff0000) shr 16
                sir[1] = (p and 0x00ff00) shr 8
                sir[2] = (p and 0x0000ff)

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                val sirOut = stack[stackpointer % div]

                routsum += sirOut[0]
                goutsum += sirOut[1]
                boutsum += sirOut[2]

                rinsum -= sirOut[0]
                ginsum -= sirOut[1]
                binsum -= sirOut[2]

                yi++
            }
            yw += w
        }

        for (curX in 0 until w) {
            rinsum = 0
            ginsum = 0
            binsum = 0
            routsum = 0
            goutsum = 0
            boutsum = 0
            rsum = 0
            gsum = 0
            bsum = 0
            yp = -radius * w
            for (idx in -radius..radius) {
                yi = max(0, yp) + curX
                val sir = stack[idx + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(idx)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (idx > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (idx < hm) {
                    yp += w
                }
            }
            yi = curX
            stackpointer = radius
            for (curY in 0 until h) {
                pix[yi] = (0xff000000.toInt() and pix[yi]) or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                val sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (curX == 0) {
                    vmin[curY] = min(curY + r1, hm) * w
                }
                p = curX + vmin[curY]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                val sirOut = stack[stackpointer]

                routsum += sirOut[0]
                goutsum += sirOut[1]
                boutsum += sirOut[2]

                rinsum -= sirOut[0]
                ginsum -= sirOut[1]
                binsum -= sirOut[2]

                yi += w
            }
        }

        val outBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        outBmp.setPixels(pix, 0, w, 0, 0, w, h)
        return outBmp
    }

    suspend fun saveProcessedWallpaperToInternalStorage(bitmap: Bitmap, profileId: String): String = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, "wallpaper_$profileId.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        file.absolutePath
    }
}
