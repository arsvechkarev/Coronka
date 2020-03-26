package com.arsvechkarev.storage

import android.content.Context

class Saver(filename: String, context: Context) {
  
  private val sharedPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
  
  fun has(key: String): Boolean {
    return sharedPrefs.contains(key)
  }
  
  fun get(key: String): String {
    return sharedPrefs.getString(key, null)!!
  }
  
  fun getOrDefault(key: String, defValue: String): String {
    return sharedPrefs.getString(key, defValue) ?: defValue
  }
  
  fun save(key: String, value: String) {
    sharedPrefs.edit().putString(key, value).apply()
  }
}