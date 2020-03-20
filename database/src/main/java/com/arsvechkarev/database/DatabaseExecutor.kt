package com.arsvechkarev.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker

class DatabaseExecutor(
  private val backgroundWorker: Worker = BackgroundWorker.io(),
  private val mainThreadWorker: Worker = MainThreadWorker()
) {
  
  fun insertOrUpdate(tableName: String, idColumnName: String, id: Int, values: ContentValues) {
    backgroundWorker.submit {
      val database = DatabaseManager.instance.writableDatabase
      val insertId = database.insertWithOnConflict(tableName, null, values, CONFLICT_IGNORE)
      if (insertId == -1L) {
        database.update(tableName, values, "$idColumnName=?", arrayOf(id.toString()))
      }
      database.close()
    }
  }
  
  fun <V> executeQuery(
    query: String,
    vararg args: String,
    transformer: (Cursor) -> V,
    callback: (V) -> Unit
  ) {
    backgroundWorker.submit {
      val cursor = DatabaseManager.getCursorSync(query, *args)
      val value = transformer(cursor)
      mainThreadWorker.submit { callback(value) }
    }
  }
  
}