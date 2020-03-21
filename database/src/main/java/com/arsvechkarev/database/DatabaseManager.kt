package com.arsvechkarev.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import core.log.Loggable
import core.log.debug

class DatabaseManager internal constructor(context: Context) :
  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), Loggable {
  
  override val tag = "DatabaseManager"
  
  override fun onCreate(db: SQLiteDatabase) {
    debug { "Database is created" }
    db.execSQL(Queries.SQL_CREATE_COUNTRIES_TABLE)
  }
  
  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
  
  companion object {
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "countries_data.db"
    
    lateinit var instance: DatabaseManager
      private set
    
    fun init(context: Context) {
      instance = DatabaseManager(context)
    }
  }
}