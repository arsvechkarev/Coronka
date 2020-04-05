package com.arsvechkarev.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

abstract class AssetsDatabaseHelper(
  private val context: Context,
  private val assetsName: String,
  version: Int
) : SQLiteOpenHelper(context, assetsName, null, version) {
  
  init {
    createDatabaseIfNeeded()
  }
  
  override fun onCreate(db: SQLiteDatabase) {
    // do nothing, because result will be overridden
  }
  
  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
  
  }
  
  fun createDatabaseIfNeeded() {
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
  
  @Throws(IOException::class)
  private fun copyDatabase(dbFile: File) {
    val inputStream: InputStream = context.assets.open(assetsName)
    val outputStream: OutputStream = FileOutputStream(dbFile)
    val buffer = ByteArray(1024)
    while (inputStream.read(buffer) > 0) {
      outputStream.write(buffer)
    }
    outputStream.flush()
    outputStream.close()
    inputStream.close()
  }
}
