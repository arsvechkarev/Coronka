package com.arsvechkarev.rankings.domain

import base.extensions.collectToMap
import base.extensions.intOfColumn
import base.extensions.stringOfColumn
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.TABLE_NAME
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.iso2
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.population
import com.arsvechkarev.common.domain.CountriesMetaInfoDatabaseConstants.world_region
import core.Database
import core.model.data.CountryMetaInfo
import io.reactivex.Single

/**
 * Data source for retrieving countries meta information
 */
fun interface CountriesMetaInfoDataSource {
  
  /**
   * Returns countries map with keys as **iso2** and values as **[CountryMetaInfo]**
   */
  fun getCountriesMetaInfo(): Single<Map<String, CountryMetaInfo>>
}

class DatabaseCountriesMetaInfoDataSource(
  private val database: Database,
) : CountriesMetaInfoDataSource {
  
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
}