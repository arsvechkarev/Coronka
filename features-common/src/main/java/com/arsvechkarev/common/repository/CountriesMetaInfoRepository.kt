package com.arsvechkarev.common.repository

import base.extensions.collectToMap
import base.extensions.intOfColumn
import base.extensions.stringOfColumn
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.TABLE_NAME
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.iso2
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.lat
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.lng
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.population
import com.arsvechkarev.common.repository.CountriesMetaInfoDatabaseSchema.world_region
import core.Database
import core.model.data.CountryMetaInfo
import core.model.data.Location
import io.reactivex.Single

/**
 * Data source for retrieving countries meta information
 */
interface CountriesMetaInfoRepository {
  
  /**
   * Returns countries map with keys as **iso2** and values as **[CountryMetaInfo]**
   */
  fun getCountriesMetaInfo(): Single<Map<String, CountryMetaInfo>>
  
  /**
   * Returns countries map with keys as **iso2** and values as **[Location]**
   * wrapped as [Single]
   */
  fun getLocationsMap(): Single<Map<String, Location>>
}

class CountriesMetaInfoRepositoryImpl(
  private val database: Database
) : CountriesMetaInfoRepository {
  
  override fun getCountriesMetaInfo() = Single.fromCallable {
    database.query(
      sql = "SELECT $iso2, $population, $world_region FROM $TABLE_NAME",
      converter = {
        collectToMap<String, CountryMetaInfo> {
          key { stringOfColumn(iso2) }
          value {
            CountryMetaInfo(
              stringOfColumn(iso2),
              intOfColumn(population),
              stringOfColumn(world_region)
            )
          }
        }
      })
  }
  
  override fun getLocationsMap() = Single.fromCallable<Map<String, Location>> {
    database.query(
      sql = "SELECT $iso2, $lat, $lng FROM $TABLE_NAME WHERE $lat IS NOT NULL",
      converter = {
        collectToMap<String, Location> {
          key { stringOfColumn(iso2) }
          value {
            val lat = stringOfColumn(lat)
            val lng = stringOfColumn(lng)
            Location(lat.toDouble(), lng.toDouble())
          }
        }
      })
  }
  
  companion object {
    
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "countries_meta_info.db"
  }
}