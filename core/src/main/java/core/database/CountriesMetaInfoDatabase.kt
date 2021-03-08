package core.database

import android.content.Context
import android.database.Cursor

class CountriesMetaInfoDatabase(context: Context) :
  AssetsDatabase(context, DATABASE_NAME, DATABASE_VERSION), Database {
  
  override fun <T> query(sql: String, converter: Cursor.() -> T): T {
    createDatabaseIfNeeded()
    readableDatabase.use { database ->
      return database.rawQuery(sql, null).use(converter)
    }
  }
  
  companion object {
    
    private const val DATABASE_VERSION = 1
    private const val DATABASE_NAME = "countries_meta_info.db"
  }
}