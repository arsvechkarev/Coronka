package core

import android.database.Cursor

/**
 * Represents an in-memory sql database
 */
interface Database {
  
  /**
   * Queries given [sql] synchronously, applies received cursor to function
   * and returns value [T]
   */
  fun <T> query(sql: String, converter: Cursor.() -> T): T
}