package com.arsvechkarev.common

import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.TABLE_NAME
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.iso2
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.lat
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.lng
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.population
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabase.Companion.world_region
import core.extenstions.assertThat
import core.extenstions.collectToList
import core.extenstions.intOfColumn
import core.extenstions.iterate
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
      collectToList {
        CountryMetaInfo(
          stringOfColumn(iso2),
          intOfColumn(population),
          stringOfColumn(world_region)
        )
      }
    })
  
  fun getLocationsMap() = Observable.fromCallable<Map<String, Location>> lb@{
    val iso2ToLocations = HashMap<String, Location>()
    database.query(
      sql = "SELECT $iso2, $lat, $lng FROM $TABLE_NAME WHERE $lat IS NOT NULL",
      function = {
        iterate {
          val iso2 = getString(getColumnIndex(iso2))
          val lat = getString(getColumnIndex(lat))
          val lng = getString(getColumnIndex(lng))
          iso2ToLocations[iso2] = Location(lat.toDouble(), lng.toDouble())
        }
      })
    assertThat(iso2ToLocations.isNotEmpty())
    return@lb iso2ToLocations
  }
}