package com.example.aifiletransfer.ai

import android.graphics.Bitmap

object HeuristicPhoneDetector {
  fun detectBox(bitmap: Bitmap): FloatArray {
    val marginX = 0.3f
    val marginY = 0.25f
    val l = 0.5f - (1f - marginX) / 2f
    val r = 0.5f + (1f - marginX) / 2f
    val t = 0.5f - (1f - marginY) / 2f
    val b = 0.5f + (1f - marginY) / 2f
    return floatArrayOf(l, t, r, b)
  }
}