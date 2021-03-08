package com.arsvechkarev.common

import com.arsvechkarev.storage.Database
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.TABLE_NAME
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.iso2
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.lat
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.lng
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.population
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseSchema.world_region
import core.extenstions.collectToMap
import core.extenstions.intOfColumn
import core.extenstions.stringOfColumn
import core.model.CountryMetaInfo
import core.model.Location
import io.reactivex.Observable

class CountriesMetaInfoRepository(
  private val database: Database
) {
  
  fun getCountriesMetaInfoSync() = database.query(
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
  
  fun getLocationsMap() = Observable.fromCallable<Map<String, Location>> lb@{
    return@lb database.query(
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
}