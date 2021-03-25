package com.arsvechkarev.map.domain

import core.model.domain.Country
import core.model.ui.CountryOnMapMetaInfo
import io.reactivex.Observable

/**
 * Use case for accessing map with countries
 */
interface MapInteractor {
  
  /**
   * Returns map with keys as **iso2** to [CountryOnMapMetaInfo]
   */
  fun requestCountriesMap(): Observable<Map<String, CountryOnMapMetaInfo>>
  
  /**
   * Returns country by given [id]
   */
  fun getCountryById(id: String): Country
}

