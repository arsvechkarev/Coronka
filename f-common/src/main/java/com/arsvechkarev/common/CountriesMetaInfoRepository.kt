package com.arsvechkarev.common

import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.TABLE_NAME
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.iso2
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.lat
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.lng
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.population
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.world_region
import core.extenstions.collectToMap
import core.extenstions.intOfColumn
import core.extenstions.stringOfColumn
import core.model.CountryMetaInfo
import core.model.Location
import io.reactivex.Observable

class CountriesMetaInfoRepository(
  private val database: CountriesMetaInfoDatabase
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