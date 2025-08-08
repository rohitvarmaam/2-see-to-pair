package com.example.aifiletransfer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.aifiletransfer.R
import com.example.aifiletransfer.ai.HeuristicPhoneDetector
import com.example.aifiletransfer.sensors.SensorReader
import com.example.aifiletransfer.utils.FileUtils
import com.example.aifiletransfer.utils.ImageUtils
import com.example.aifiletransfer.utils.LabelUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
  private var imageCapture: ImageCapture? = null

  private val requestPermissions = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { perms ->
    val granted = perms.values.all { it }
    if (granted) startCamera() else Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    askPermissionsAndStart()
    findViewById<Button>(R.id.btnCapture).setOnClickListener { captureAndDetect() }
    SensorReader.start(this)
  }

  private fun askPermissionsAndStart() {
    val needs = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      // no additional perms needed
    }
    if (!needs.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
      requestPermissions.launch(needs.toTypedArray())
    } else startCamera()
  }

  private fun startCamera() {
    val providerFuture = ProcessCameraProvider.getInstance(this)
    providerFuture.addListener({
      val cameraProvider = providerFuture.get()
      val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(findViewById<androidx.camera.view.PreviewView>(R.id.previewView).surfaceProvider)
      }
      imageCapture = ImageCapture.Builder().build()

      try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
      } catch (_: Exception) {}
    }, ContextCompat.getMainExecutor(this))
  }

  private fun captureAndDetect() {
    val imgCap = imageCapture ?: return
    val ds = FileUtils.datasetDir(this)
    val rawDir = File(ds, "raw").apply { mkdirs() }
    val cropDir = File(ds, "crops").apply { mkdirs() }
    val metaDir = File(ds, "meta").apply { mkdirs() }

    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(System.currentTimeMillis())
    val photoFile = File(rawDir, "$stamp.jpg")
    val output = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imgCap.takePicture(output, ContextCompat.getMainExecutor(this),
      object: ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
          Toast.makeText(this@CameraActivity, "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
          val bmp = BitmapFactory.decodeFile(photoFile.absolutePath)
          val box = HeuristicPhoneDetector.detectBox(bmp)
          val cropped = ImageUtils.cropNormalized(bmp, box)
          val cropFile = File(cropDir, "$stamp.jpg")
          ImageUtils.saveBitmap(cropped, cropFile)

          val meta = SensorReader.snapshot()
          val bucket = LabelUtils.headingBucket(meta.toString())
          meta.put("heading_bucket", bucket)
          File(metaDir, "$stamp.json").writeText(meta.toString())

          Toast.makeText(this@CameraActivity, "Saved dataset sample.", Toast.LENGTH_SHORT).show()
        }
      })
  }

  override fun onDestroy() {
    super.onDestroy()
    SensorReader.stop(this)
  }
}