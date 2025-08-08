package com.example.aifiletransfer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.aifiletransfer.R

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.btnSend).setOnClickListener {
      startActivity(Intent(this, CameraActivity::class.java))
    }
    findViewById<Button>(R.id.btnReceive).setOnClickListener {
      startActivity(Intent(this, TrainActivity::class.java))
    }
  }
}