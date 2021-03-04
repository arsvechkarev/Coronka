package com.arsvechkarev.storage.countries

import android.database.Cursor

/**
 * Database with countries meta information: iso2, population, world region, latitude and longitude
 */
interface CountriesMetaInfoDatabase {
  
  /**
   * Queries given [sql] synchronously, applies received cursor to function
   * and returns value [T]
   */
  fun <T> query(sql: String, converter: Cursor.() -> T): T
  
  /** A schema for database */
  companion object {
    
    const val TABLE_NAME = "countries_meta_info"
    
    const val iso2 = "iso2"
    const val population = "population"
    const val world_region = "world_region"
    const val lat = "lat"
    const val lng = "lng"
  }
}