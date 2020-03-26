package com.arsvechkarev.network

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8


class Networker {
  
  fun syncRequest(url: String): String {
    val stringBuilder = StringBuilder()
    val urlInstance = URL(url)
    val urlConnection = urlInstance.openConnection() as HttpURLConnection
    try {
      val reader = BufferedReader(InputStreamReader(urlConnection.inputStream, UTF_8))
      reader.use {
        while (true) {
          val c = reader.read()
          if (c == -1) break
          stringBuilder.append(c)
        }
      }
    } finally {
      urlConnection.disconnect()
    }
    return stringBuilder.toString()
  }
  
}