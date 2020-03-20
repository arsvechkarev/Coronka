package com.arsvechkarev.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper internal constructor(context: Context) :
  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
  
  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(SQL_CREATE_COUNTRIES_TABLE)
  }
  
  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
  
  companion object {
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "countries_data.db"
  }
}