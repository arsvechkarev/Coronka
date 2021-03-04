package com.arsvechkarev.storage

import android.database.Cursor
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase

class DatabaseImpl(
  private val databaseHelper: DatabaseHelper
) : CountriesMetaInfoDatabase {
  
  override fun <T> query(sql: String, converter: Cursor.() -> T): T {
    databaseHelper.configureIfNeeded()
    databaseHelper.readableDb.use { database ->
      return database.rawQuery(sql, null).use(converter)
    }
  }
}