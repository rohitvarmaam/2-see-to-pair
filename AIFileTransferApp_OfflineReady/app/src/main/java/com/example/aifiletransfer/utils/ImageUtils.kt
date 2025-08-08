package com.example.aifiletransfer.utils

import android.graphics.*
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
  fun cropNormalized(src: Bitmap, box: FloatArray): Bitmap {
    val l = (box[0] * src.width).toInt().coerceIn(0, src.width - 1)
    val t = (box[1] * src.height).toInt().coerceIn(0, src.height - 1)
    val r = (box[2] * src.width).toInt().coerceIn(l + 1, src.width)
    val b = (box[3] * src.height).toInt().coerceIn(t + 1, src.height)
    return Bitmap.createBitmap(src, l, t, r - l, b - t)
  }
  fun saveBitmap(bmp: Bitmap, file: File) { FileOutputStream(file).use { out -> bmp.compress(Bitmap.CompressFormat.JPEG, 95, out) } }
}