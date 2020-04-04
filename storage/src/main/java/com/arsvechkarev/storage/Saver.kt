package com.arsvechkarev.storage

import android.content.Context
import android.content.SharedPreferences

class Saver(filename: String, context: Context) {
  
  private val sharedPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
  
  fun has(key: String): Boolean {
    return sharedPrefs.contains(key)
  }
  
  fun getString(key: String): String {
    return sharedPrefs.getString(key, null)!!
  }
  
  fun getInt(key: String): Int {
    return sharedPrefs.getInt(key, Int.MAX_VALUE)
  }
  
  fun getOrDefault(key: String, defValue: String): String {
    return sharedPrefs.getString(key, defValue) ?: defValue
  }
  
  fun execute(synchronosly: Boolean = false, block: SharedPreferences.Editor.() -> Unit) {
    val editor = sharedPrefs.edit()
    block(editor)
    if (synchronosly) {
      editor.commit()
    } else {
      editor.apply()
    }
  }
}