package coreimpl

import android.content.Context
import android.database.Cursor
import core.Database

class AssetsDatabaseImpl(
  context: Context, databaseName: String, databaseVersion: Int
) : AssetsDatabase(context, databaseName, databaseVersion), Database {
  
  override fun <T> query(sql: String, converter: Cursor.() -> T): T {
    createDatabaseIfNeeded()
    readableDatabase.use { database ->
      return database.rawQuery(sql, null).use(converter)
    }
  }
}