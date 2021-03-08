package core.datasources

import core.model.CountryMetaInfo
import core.model.Location
import io.reactivex.Observable

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
   * wrapped as [Observable]
   */
  fun getLocationsMap(): Observable<Map<String, Location>>
}