package com.arsvechkarev.database

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker

object DatabaseHolder {
  
  lateinit var instance: DatabaseHelper
    private set
  
  fun init(context: Context) {
    instance = DatabaseHelper(context)
  }
}