package com.arsvechkarev.database

import android.content.ContentValues
import android.database.Cursor
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker

class DatabaseExecutor(
  private val backgroundWorker: Worker = BackgroundWorker.default(),
  private val mainThreadWorker: Worker = MainThreadWorker()
) {
  
  fun insert(tableName: String, values: ContentValues) {
    backgroundWorker.execute {
      DatabaseManager.instance.writableDatabase.insert(tableName, null, values)
    }
  }
  
  fun executeInBackground(block: () -> Unit) {
    backgroundWorker.execute(block)
  }
  
  fun <V> executeQuery(
    query: String,
    vararg args: String,
    transformer: (Cursor) -> V,
    callback: (V) -> Unit
  ) {
    backgroundWorker.execute {
      val cursor = DatabaseManager.getCursorSync(query, *args)
      val value = transformer(cursor)
      mainThreadWorker.execute { callback(value) }
    }
  }
  
}