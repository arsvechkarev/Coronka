package com.arsvechkarev.storage

import android.content.Context
import core.Loggable

class DatabaseManager internal constructor(
  context: Context
) : AssetsDatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION), Loggable {
  
  override val logTag = "DatabaseManager"
  
  companion object {
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "countries.db"
    
    lateinit var instance: DatabaseManager
      private set
  
    // Should only be called once to avoid simultaneous creation of the database
    fun init(context: Context) {
      instance = DatabaseManager(context)
    }
  }
}