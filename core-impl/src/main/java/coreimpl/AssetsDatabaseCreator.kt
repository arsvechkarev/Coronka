package coreimpl

import android.content.Context
import core.Database
import core.di.DatabaseCreator

class AssetsDatabaseCreator(private val context: Context) : DatabaseCreator {
  
  override fun provideDatabase(databaseName: String, databaseVersion: Int): Database {
    return AssetsDatabaseImpl(context, databaseName, databaseVersion)
  }
}