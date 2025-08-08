package com.example.aifiletransfer.utils

import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.roundToInt

object LabelUtils {
  fun headingBucket(metaJson: String): Int {
    val obj = JSONObject(metaJson)
    val mag = obj.optJSONArray("mag") ?: return -1
    val x = mag.optDouble(0, 0.0)
    val y = mag.optDouble(1, 0.0)
    val heading = Math.toDegrees(atan2(y, x)).let { if (it < 0) it + 360 else it }
    return ((heading / 45.0).roundToInt()) % 8
  }
}