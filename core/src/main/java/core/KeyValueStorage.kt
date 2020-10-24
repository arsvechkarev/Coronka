package core

import android.content.SharedPreferences

/**
 * Represents a key-value storage
 */
interface KeyValueStorage {
  
  fun hasString(key: String): Boolean
  
  fun getString(key: String): String
  
  fun putString(key: String, value: String)
  
  fun hasLong(key: String): Boolean
  
  fun getLong(key: String): Long
  
  fun putLong(key: String, value: Long)
  
  fun hasInt(key: String): Boolean
  
  fun getInt(key: String): Int
  
  fun putInt(key: String, value: Int)
  
  fun execute(block: SharedPreferences.Editor.() -> Unit)
}