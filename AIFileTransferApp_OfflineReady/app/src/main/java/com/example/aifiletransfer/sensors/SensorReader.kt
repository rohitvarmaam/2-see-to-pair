package com.example.aifiletransfer.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference

object SensorReader : SensorEventListener {
  private var sensorManager: SensorManager? = null
  private val last = AtomicReference(JSONObject())

  fun start(context: Context) {
    sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sm = sensorManager ?: return
    listOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD).forEach { type ->
      sm.getDefaultSensor(type)?.also { sensor ->
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
      }
    }
  }

  fun stop(context: Context) {
    sensorManager?.unregisterListener(this)
  }

  fun snapshot(): JSONObject = JSONObject(last.get().toString())

  override fun onSensorChanged(event: SensorEvent) {
    val obj = JSONObject(last.get().toString())
    val key = when (event.sensor.type) {
      Sensor.TYPE_ACCELEROMETER -> "accel"
      Sensor.TYPE_GYROSCOPE -> "gyro"
      Sensor.TYPE_MAGNETIC_FIELD -> "mag"
      else -> "other"
    }
    obj.put(key, listOf(event.values[0], event.values[1], event.values[2]))
    obj.put("timestamp", System.currentTimeMillis())
    last.set(obj)
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}