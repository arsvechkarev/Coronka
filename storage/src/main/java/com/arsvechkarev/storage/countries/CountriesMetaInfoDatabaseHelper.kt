package com.arsvechkarev.storage.countries

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.arsvechkarev.storage.AssetsDatabase
import com.arsvechkarev.storage.DatabaseHelper

class CountriesMetaInfoDatabaseHelper internal constructor(
  context: Context
) : AssetsDatabase(context, DATABASE_NAME, DATABASE_VERSION), DatabaseHelper {
  
  override val readableDb: SQLiteDatabase
    get() = instance.readableDatabase
  
  override val writableDb: SQLiteDatabase
    get() = instance.writableDatabase
  
  override fun configureIfNeeded() {
    createDatabaseIfNeeded()
  }
  
  companion object {
    
    private const val DATABASE_VERSION = 1
    private const val DATABASE_NAME = "countries_meta_info.db"
  
    lateinit var instance: CountriesMetaInfoDatabaseHelper
      private set
    
    // Should be called from one thread
    fun init(context: Context) {
      instance = CountriesMetaInfoDatabaseHelper(context)
    }
  }
}