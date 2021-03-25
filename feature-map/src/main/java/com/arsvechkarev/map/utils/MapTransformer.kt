package com.arsvechkarev.map.utils

import core.model.data.Location
import core.model.domain.Country
import core.model.ui.CountryOnMapMetaInfo

class MapTransformer {
  
  fun transformResult(
    countries: List<Country>,
    locationsMap: Map<String, Location>
  ): Map<String, CountryOnMapMetaInfo> {
    val map = HashMap<String, CountryOnMapMetaInfo>()
    for (country in countries) {
      val location = locationsMap[country.iso2] ?: continue
      map[country.iso2] = CountryOnMapMetaInfo(country.id, country.confirmed, location)
    }
    return map
  }
}