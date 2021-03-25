package com.arsvechkarev.map

import com.arsvechkarev.map.domain.LocationsMapDataSource
import core.model.data.Location
import io.reactivex.Single

val FakeLocationsMap = mapOf(
  "US" to Location(2.36, 2.0),
  "CA" to Location(1.898, 6.2),
  "UK" to Location(3.0, 3.1),
  "FR" to Location(12.0, 4.86),
  "DE" to Location(9.0, 3.34),
  "CN" to Location(6.2, 1.0),
  "IN" to Location(7.0, 4.9),
  "BR" to Location(2.59, 12.87),
  "AU" to Location(8.0, 2.13),
)

class FakeLocationsDataSource : LocationsMapDataSource {
  
  override fun getLocationsMap(): Single<Map<String, Location>> {
    return Single.just(FakeLocationsMap)
  }
}