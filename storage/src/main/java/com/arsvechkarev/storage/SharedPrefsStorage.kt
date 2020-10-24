package com.arsvechkarev.storage

import android.content.Context
import android.content.SharedPreferences
import core.CacheValidationStorage
import core.datetime.MillisDateTime
import core.extenstions.assertThat
import java.util.concurrent.TimeUnit

class SharedPrefsStorage(filename: String, context: Context) : CacheValidationStorage {
  
  private val sharedPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
  
  override fun hasString(key: String): Boolean {
    return sharedPrefs.getString(key, null) != null
  }
  
  override fun getString(key: String): String {
    return sharedPrefs.getString(key, null)!!
  }
  
  override fun putString(key: String, value: String) {
    sharedPrefs.edit().putString(key, value).apply()
  }
  
  override fun hasLong(key: String): Boolean {
    return sharedPrefs.getLong(key, Long.MAX_VALUE) != Long.MAX_VALUE
  }
  
  override fun getLong(key: String): Long {
    return sharedPrefs.getLong(key, Long.MAX_VALUE)
  }
  
  override fun putLong(key: String, value: Long) {
    return sharedPrefs.edit().putLong(key, value).apply()
  }
  
  override fun hasInt(key: String): Boolean {
    return sharedPrefs.getInt(key, Int.MAX_VALUE) != Int.MAX_VALUE
  }
  
  override fun getInt(key: String): Int {
    return sharedPrefs.getInt(key, Int.MAX_VALUE)
  }
  
  override fun putInt(key: String, value: Int) {
    return sharedPrefs.edit().putInt(key, value).apply()
  }
  
  override fun isUpToDate(key: String, maxMinutesInCache: Int): Boolean {
    return sharedPrefs.contains(key) && cacheIsValid(key, maxMinutesInCache)
  }
  
  override fun execute(block: SharedPreferences.Editor.() -> Unit) {
    val editor = sharedPrefs.edit()
    block(editor)
    editor.apply()
  }
  
  private fun cacheIsValid(key: String, maxMinutesInCache: Int): Boolean {
    val cacheDate = MillisDateTime.ofMillis(sharedPrefs.getLong(key, Long.MAX_VALUE))
    assertThat(cacheDate.millis != Long.MAX_VALUE) { "No date in cache with key $key" }
    return cacheDate.differenceWith(MillisDateTime.current(), TimeUnit.MINUTES) < maxMinutesInCache
  }
}
