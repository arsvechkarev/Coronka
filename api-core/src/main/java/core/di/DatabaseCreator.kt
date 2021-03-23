package core.di

import core.Database

/**
 * Creates instances of [Database]
 */
interface DatabaseCreator {
  
  /**
   * Creates database with name: [databaseName] and version: [databaseVersion]
   */
  fun provideDatabase(databaseName: String, databaseVersion: Int): Database
}