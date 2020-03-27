package com.arsvechkarev.network

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class Networker {
  
  fun performRequest(url: String): String {
    val stringBuilder = StringBuilder()
    val urlInstance = URL(url)
    BufferedReader(InputStreamReader(urlInstance.openStream())).use {
      while (true) {
        val line = it.readLine() ?: break
        stringBuilder.append(line)
      }
    }
    return stringBuilder.toString()
  }
}