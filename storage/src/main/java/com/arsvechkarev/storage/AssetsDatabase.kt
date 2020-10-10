package com.arsvechkarev.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

abstract class AssetsDatabase(
  private val context: Context,
  private val assetsName: String,
  version: Int
) : SQLiteOpenHelper(context, assetsName, null, version) {
  
  override fun onCreate(db: SQLiteDatabase) {
    // do nothing, because all necessary tables are already in the database from assets
  }
  
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
  
  }
  
  protected fun createDatabaseIfNeeded() {
    val dbFile: File = context.getDatabasePath(assetsName)
    if (!dbFile.exists()) {
      try {
        // Creating database if not exists
        this.readableDatabase
        this.close()
        
        // Overwriting created database with file from assets
        copyDatabase(dbFile)
        
      } catch (e: IOException) {
        throw RuntimeException("Error creating source database", e)
      }
    }
  }
  
  private fun copyDatabase(dbFile: File) {
    context.assets.open(assetsName).use { inputStream ->
      FileOutputStream(dbFile).use { outputStream ->
        val buffer = ByteArray(1024)
        while (inputStream.read(buffer) > 0) {
          outputStream.write(buffer)
        }
        outputStream.flush()
      }
    }
  }
}
