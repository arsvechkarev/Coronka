package com.arsvechkarev.database

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE


class DatabaseExecutor {
  
  fun isTableNotEmpty(tableName: String): Boolean {
    DatabaseManager.instance.readableDatabase.use {
      val rowsCount = DatabaseUtils.queryNumEntries(it, tableName)
      return rowsCount != 0L
    }
  }
  
  fun insertOrUpdate(
    database: SQLiteDatabase,
    tableName: String,
    idColumnName: String,
    id: Int,
    values: ContentValues
  ) {
    DatabaseManager.executeInBackground {
      val insertId = database.insertWithOnConflict(tableName, null, values, CONFLICT_IGNORE)
      if (insertId == -1L) {
        database.update(tableName, values, "$idColumnName=?", arrayOf(id.toString()))
      }
    }
  }
  
  fun <V> executeQuery(
    database: SQLiteDatabase,
    query: String,
    transformer: (Cursor) -> V,
    callback: (V) -> Unit
  ) {
    DatabaseManager.backgroundWorker.submit {
      val cursor = DatabaseManager.getCursorSync(database, query)
      val value = transformer(cursor)
      DatabaseManager.mainThreadWorker.submit { callback(value) }
    }
  }
  
}