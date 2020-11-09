package com.arsvechkarev.storage

import android.database.Cursor
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase
import com.arsvechkarev.storage.countries.Database

class DatabaseImpl(
  private val databaseHelper: DatabaseHelper
) : CountriesMetaInfoDatabase, Database {
  
  override fun query(sql: String, function: Cursor.() -> Unit) {
    databaseHelper.configureIfNeeded()
    databaseHelper.readableDb.use { database ->
      val rawQuery = database.rawQuery(sql, null)
      function(rawQuery)
    }
  }
  
  override fun <T> query(sql: String, converter: Cursor.() -> T): T {
    databaseHelper.configureIfNeeded()
    databaseHelper.readableDb.use { database ->
      return database.rawQuery(sql, null).use(converter)
    }
  }
}