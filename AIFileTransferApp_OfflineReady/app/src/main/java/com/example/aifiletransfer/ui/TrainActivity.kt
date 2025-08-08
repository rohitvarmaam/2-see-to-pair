package com.example.aifiletransfer.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aifiletransfer.R
import com.example.aifiletransfer.ai.EmbeddingClassifier
import com.example.aifiletransfer.utils.FileUtils
import java.io.File

class TrainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_train)

    findViewById<Button>(R.id.btnTrain).setOnClickListener {
      try {
        val ds = FileUtils.datasetDir(this)
        val crops = File(ds, "crops").listFiles()?.sorted() ?: emptyList()
        val metas = File(ds, "meta").listFiles()?.sorted() ?: emptyList()

        if (crops.isEmpty() || metas.isEmpty()) {
          Toast.makeText(this, "Collect some data first!", Toast.LENGTH_LONG).show()
          return@setOnClickListener
        }

        val classifier = EmbeddingClassifier()
        classifier.train(crops, metas)
        val modelStore = File(filesDir, "trained_centroids.json")
        classifier.save(modelStore)

        val sampleBmp = BitmapFactory.decodeFile(crops.first().absolutePath)
        val pred = classifier.predict(sampleBmp)
        Toast.makeText(this, "Training done. Example prediction bucket=$pred", Toast.LENGTH_LONG).show()
      } catch (e: Exception) {
        Toast.makeText(this, "Training error: ${e.message}", Toast.LENGTH_LONG).show()
      }
    }
  }
}