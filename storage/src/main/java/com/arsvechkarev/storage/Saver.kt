package com.arsvechkarev.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import core.DateTime
import core.extenstions.assertThat
import java.util.concurrent.TimeUnit

class Saver(filename: String, context: Context) {
  
  private val sharedPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
  
  fun getString(key: String): String {
    return sharedPrefs.getString(key, null)!!
  }
  
  fun getInt(key: String): Int {
    return sharedPrefs.getInt(key, Int.MAX_VALUE)
  }
  
  fun isUpToDate(key: String, maxMinutesInCache: Int): Boolean {
    return sharedPrefs.contains(key) && cacheIsValid(key, maxMinutesInCache)
  }
  
  @SuppressLint("ApplySharedPref")
  fun execute(synchronously: Boolean = false, block: SharedPreferences.Editor.() -> Unit) {
    val editor = sharedPrefs.edit()
    block(editor)
    if (synchronously) {
      editor.commit()
    } else {
      editor.apply()
    }
  }
  
  private fun cacheIsValid(key: String, maxMinutesInCache: Int): Boolean {
    val cacheDate = DateTime.ofMillis(sharedPrefs.getLong(key, Long.MAX_VALUE))
    assertThat(cacheDate.millis != Long.MAX_VALUE) { "No date in cache with key $key" }
    return cacheDate.differenceWith(DateTime.current(), TimeUnit.MINUTES) < maxMinutesInCache
  }
}