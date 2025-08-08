package com.example.aifiletransfer.ai

import android.graphics.Bitmap

object SimpleFeatureExtractor {
  fun embed(bitmap: Bitmap): FloatArray {
    val bins = FloatArray(64) { 0f }
    val w = bitmap.width
    val h = bitmap.height
    val stepX = (w / 224f).coerceAtLeast(1f)
    val stepY = (h / 224f).coerceAtLeast(1f)
    var y = 0f
    while (y < h) {
      var x = 0f
      while (x < w) {
        val px = bitmap.getPixel(x.toInt(), y.toInt())
        val r = (px shr 16) and 0xFF
        val g = (px shr 8) and 0xFF
        val b = (px) and 0xFF
        val rb = r / 64
        val gb = g / 64
        val bb = b / 64
        val idx = rb * 16 + gb * 4 + bb
        bins[idx] += 1f
        x += stepX
      }
      y += stepY
    }
    var norm = 0f
    for (v in bins) norm += v * v
    norm = kotlin.math.sqrt(norm)
    if (norm > 0f) for (i in bins.indices) bins[i] /= norm
    return bins
  }
}