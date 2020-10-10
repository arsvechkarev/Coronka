package com.arsvechkarev.storage

import android.database.sqlite.SQLiteDatabase

/**
 * Helper that provides readable and writable database and could also be configured
 */
interface DatabaseHelper {
  
  /** Returns database for read-only operations */
  val readableDb: SQLiteDatabase
  
  /** Returns database for read and write operations */
  val writableDb: SQLiteDatabase
  
  /** Configures database (if necessary) before usage */
  fun configureIfNeeded()
}