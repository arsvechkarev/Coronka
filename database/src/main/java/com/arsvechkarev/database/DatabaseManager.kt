package com.arsvechkarev.database

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker

object DatabaseManager {
  internal val backgroundWorker: Worker = BackgroundWorker.io()
  internal val mainThreadWorker: Worker = MainThreadWorker()
  
  lateinit var instance: DatabaseHelper
    private set
  
  fun init(context: Context) {
    instance = DatabaseHelper(context)
  }
  
  fun executeInBackground(block: () -> Unit) {
    backgroundWorker.submit(block)
  }
  
  fun getCursorSync(database: SQLiteDatabase, query: String): Cursor {
    return database.rawQuery(query, null)
  }
}