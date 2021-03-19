package core.transformers

import core.model.CountryOnMap
import core.model.Location
import core.model.TotalInfo

object MapTransformer {
  
  fun transformResult(pair: Pair<TotalInfo, Map<String, Location>>): Map<String, CountryOnMap> {
    val map = HashMap<String, CountryOnMap>()
    for (country in pair.first.countries) {
      val location = pair.second[country.iso2] ?: continue
      map[country.iso2] = CountryOnMap(country, location)
    }
    return map
  }
  
  fun transformResult(totalInfo: TotalInfo, locationsMap: Map<String, Location>): Map<String, CountryOnMap> {
    val map = HashMap<String, CountryOnMap>()
    for (country in totalInfo.countries) {
      val location = locationsMap[country.iso2] ?: continue
      map[country.iso2] = CountryOnMap(country, location)
    }
    return map
  }
}