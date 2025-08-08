package com.example.aifiletransfer.ai

import android.graphics.Bitmap
import org.json.JSONObject
import java.io.File
import java.nio.file.Files

class EmbeddingClassifier {
  private val centroids = mutableMapOf<Int, FloatArray>()
  private var dim: Int = -1

  fun train(crops: List<File>, metas: List<File>) {
    val byLabel = mutableMapOf<Int, MutableList<FloatArray>>()
    for (i in crops.indices) {
      val img = crops[i]
      val meta = metas.getOrNull(i) ?: continue
      val label = JSONObject(meta.readText()).optInt("heading_bucket", -1)
      if (label == -1) continue
      val bmp = android.graphics.BitmapFactory.decodeFile(img.absolutePath)
      val emb = SimpleFeatureExtractor.embed(bmp)
      dim = emb.size
      byLabel.getOrPut(label) { mutableListOf() }.add(emb)
    }
    centroids.clear()
    for ((label, embs) in byLabel) {
      val c = FloatArray(dim) { 0f }
      for (v in embs) for (j in 0 until dim) c[j] += v[j]
      for (j in 0 until dim) c[j] /= (embs.size.coerceAtLeast(1))
      centroids[label] = c
    }
  }

  fun predict(bitmap: Bitmap): Int {
    val emb = SimpleFeatureExtractor.embed(bitmap)
    var best = -1
    var bestScore = Float.NEGATIVE_INFINITY
    for ((label, c) in centroids) {
      val score = cosine(emb, c)
      if (score > bestScore) { bestScore = score; best = label }
    }
    return best
  }

  fun save(file: File) {
    val obj = JSONObject()
    for ((label, c) in centroids) obj.put(label.toString(), c.toList())
    file.writeText(obj.toString())
  }

  fun load(file: File) {
    centroids.clear()
    if (!file.exists()) return
    val obj = JSONObject(String(Files.readAllBytes(file.toPath())))
    for (key in obj.keys()) {
      val arr = obj.getJSONArray(key)
      val v = FloatArray(arr.length()) { i -> arr.getDouble(i).toFloat() }
      centroids[key.toInt()] = v
    }
  }

  private fun cosine(a: FloatArray, b: FloatArray): Float {
    var dot = 0f; var na = 0f; var nb = 0f
    val n = minOf(a.size, b.size)
    for (i in 0 until n) { dot += a[i]*b[i]; na += a[i]*a[i]; nb += b[i]*b[i] }
    return dot / (kotlin.math.sqrt(na)*kotlin.math.sqrt(nb) + 1e-6f)
  }
}