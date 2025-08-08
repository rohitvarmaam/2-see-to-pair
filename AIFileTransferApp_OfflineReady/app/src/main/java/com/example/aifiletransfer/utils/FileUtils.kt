package com.example.aifiletransfer.utils

import android.content.Context
import java.io.File

object FileUtils {
  fun datasetDir(context: Context): File = File(context.filesDir, "dataset").apply { mkdirs() }
}