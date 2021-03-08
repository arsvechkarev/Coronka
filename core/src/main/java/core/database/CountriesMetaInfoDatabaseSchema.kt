package core.database

/**
 * A schema for database with countries meta information: iso2, population,
 * world region, latitude and longitude
 */
object CountriesMetaInfoDatabaseSchema {
  
  const val TABLE_NAME = "countries_meta_info"
  
  const val iso2 = "iso2"
  const val population = "population"
  const val world_region = "world_region"
  const val lat = "lat"
  const val lng = "lng"
}