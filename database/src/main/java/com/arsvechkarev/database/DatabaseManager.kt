package com.arsvechkarev.database

import android.content.Context
import android.database.Cursor

object DatabaseManager {
  
  lateinit var instance: DatabaseHelper
    private set
  
  fun init(context: Context) {
    instance = DatabaseHelper(context)
  }
  
  fun executeQuerySync(query: String) {
    instance.writableDatabase.execSQL(query)
  }
  
  fun getCursorSync(query: String, vararg selectionArgs: String): Cursor {
    return instance.readableDatabase.rawQuery(query, selectionArgs)
  }
}