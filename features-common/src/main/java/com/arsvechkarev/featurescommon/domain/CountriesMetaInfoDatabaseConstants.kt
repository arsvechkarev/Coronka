package com.arsvechkarev.featurescommon.domain

/**
 * Constants for database with countries meta information
 */
object CountriesMetaInfoDatabaseConstants {
  
  const val DATABASE_VERSION = 1
  const val DATABASE_NAME = "countries_meta_info.db"
  
  const val TABLE_NAME = "countries_meta_info"
  
  const val iso2 = "iso2"
  const val population = "population"
  const val world_region = "world_region"
  const val lat = "lat"
  const val lng = "lng"
}