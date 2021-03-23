package com.arsvechkarev.map.utils

import core.model.CountryOnMap
import core.model.Location
import core.model.TotalInfo

object MapTransformer {
  
  fun transformResult(totalInfo: TotalInfo, locationsMap: Map<String, Location>): Map<String, CountryOnMap> {
    val map = HashMap<String, CountryOnMap>()
    for (country in totalInfo.countries) {
      val location = locationsMap[country.iso2] ?: continue
      map[country.iso2] = CountryOnMap(country, location)
    }
    return map
  }
}