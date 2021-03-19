package core.datasources

import core.database.CountriesMetaInfoDatabaseSchema.TABLE_NAME
import core.database.CountriesMetaInfoDatabaseSchema.iso2
import core.database.CountriesMetaInfoDatabaseSchema.lat
import core.database.CountriesMetaInfoDatabaseSchema.lng
import core.database.CountriesMetaInfoDatabaseSchema.population
import core.database.CountriesMetaInfoDatabaseSchema.world_region
import core.database.Database
import core.extenstions.collectToMap
import core.extenstions.intOfColumn
import core.extenstions.stringOfColumn
import core.model.CountryMetaInfo
import core.model.Location
import io.reactivex.Single

/**
 * Data source for retrieving countries meta information
 */
interface CountriesMetaInfoDataSource {
  
  /**
   * Returns countries map with keys as **iso2** and values as **[CountryMetaInfo]**
   */
  fun getCountriesMetaInfoSync(): Map<String, CountryMetaInfo>
  
  /**
   * Returns countries map with keys as **iso2** and values as **[Location]**
   * wrapped as [Single]
   */
  fun getLocationsMap(): Single<Map<String, Location>>
}

class CountriesMetaInfoDataSourceImpl(
  private val database: Database
) : CountriesMetaInfoDataSource {
  
  override fun getCountriesMetaInfoSync() = database.query(
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
  
  override fun getLocationsMap() = Single.fromCallable<Map<String, Location>> lb@{
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