package com.arsvechkarev.database

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE

object DatabaseExecutor {
  
  fun isTableNotEmpty(database: SQLiteDatabase, tableName: String): Boolean {
    val rowsCount = DatabaseUtils.queryNumEntries(database, tableName)
    return rowsCount != 0L
  }
  
  fun insertOrUpdate(
    database: SQLiteDatabase,
    tableName: String,
    idColumnName: String,
    id: Int,
    values: ContentValues
  ) {
    val insertId = database.insertWithOnConflict(tableName, null, values, CONFLICT_IGNORE)
    if (insertId == -1L) {
      database.update(tableName, values, "$idColumnName=?", arrayOf(id.toString()))
    }
  }
  
  fun <V> executeQuery(
    database: SQLiteDatabase,
    query: String,
    transformer: (Cursor) -> V,
    callback: (V) -> Unit
  ) {
    val cursor = database.rawQuery(query, null)
    callback(transformer(cursor))
  }
  
}