package com.arsvechkarev.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

object DatabaseManager {
  
  lateinit var instance: DatabaseHelper
    private set
  
  fun init(context: Context) {
    instance = DatabaseHelper(context)
  }
  
  fun insert(tableName: String, values: ContentValues) {
    instance.writableDatabase.use {
      it.insert(tableName, null, values)
    }
  }
  
  fun getCursorSync(query: String, vararg selectionArgs: String): Cursor {
    return instance.readableDatabase.rawQuery(query, selectionArgs)
  }
}