package com.arsvechkarev.map.domain

import base.extensions.collectToMap
import base.extensions.stringOfColumn
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.TABLE_NAME
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.iso2
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.lat
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.lng
import core.Database
import core.model.data.Location
import io.reactivex.Single

interface LocationsMapDataSource {
  
  /**
   * Returns countries map with keys as **iso2** and values as **[Location]**
   * wrapped as [Single]
   */
  fun getLocationsMap(): Single<Map<String, Location>>
}

class DatabaseLocationsMapDataSource(
  private val database: Database
) : LocationsMapDataSource {
  
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
}