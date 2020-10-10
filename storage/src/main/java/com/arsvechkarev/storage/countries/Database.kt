package com.arsvechkarev.storage.countries

import android.database.Cursor

/**
 * Represents an sqlite database
 */
interface Database {
  
  /** Queries given [sql] synchronously and applies received cursor to function */
  fun query(sql: String, function: Cursor.() -> Unit)
  
  /**
   * Queries given [sql] synchronously, applies received cursor to function
   * and returns value [T]
   */
  fun <T> query(sql: String, converter: Cursor.() -> T): T
}