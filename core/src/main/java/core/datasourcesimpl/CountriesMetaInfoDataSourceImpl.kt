package core.datasourcesimpl

import core.database.CountriesMetaInfoDatabaseSchema.TABLE_NAME
import core.database.CountriesMetaInfoDatabaseSchema.iso2
import core.database.CountriesMetaInfoDatabaseSchema.lat
import core.database.CountriesMetaInfoDatabaseSchema.lng
import core.database.CountriesMetaInfoDatabaseSchema.population
import core.database.CountriesMetaInfoDatabaseSchema.world_region
import core.database.Database
import core.datasources.CountriesMetaInfoDataSource
import core.extenstions.collectToMap
import core.extenstions.intOfColumn
import core.extenstions.stringOfColumn
import core.model.CountryMetaInfo
import core.model.Location
import io.reactivex.Observable

class CountriesMetaInfoDataSourceImpl(private val database: Database) :
  CountriesMetaInfoDataSource {
  
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
  
  override fun getLocationsMap() = Observable.fromCallable<Map<String, Location>> lb@{
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