package com.arsvechkarev.map.domain

import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.common.domain.CountriesDataSource
import core.Mapper
import core.Schedulers
import core.model.data.CountriesWrapper
import core.model.data.CountryEntity
import core.model.data.Location
import core.model.domain.Country
import core.model.ui.CountryOnMapMetaInfo
import io.reactivex.Observable
import io.reactivex.Single

class MapInteractor(
  private val countriesDataSource: CountriesDataSource,
  private val locationsMapDataSource: LocationsMapDataSource,
  private val countriesDomainMapper: Mapper<List<CountryEntity>, List<Country>>,
  private val schedulers: Schedulers
) {
  
  private var countries: List<Country>? = null
  
  /**
   * Returns map with keys as **iso2** to [CountryOnMapMetaInfo]
   */
  fun requestCountriesMap(): Observable<Map<String, CountryOnMapMetaInfo>> {
    return Single.zip(
      countriesDataSource.requestCountries().subscribeOn(schedulers.io())
          .map(Result.Companion::success)
          .onErrorReturn(Result.Companion::failure),
      locationsMapDataSource.getLocationsMap().subscribeOn(schedulers.io())
          .map(Result.Companion::success)
          .onErrorReturn(Result.Companion::failure),
      { countries, map -> mapToResult(countries, map)() }
    ).toObservable()
        .withNetworkDelay(schedulers)
        .flatMap { result ->
          result.fold(
            onSuccess = { map -> Observable.just(map) },
            onFailure = { throwable -> Observable.error(throwable) }
          )
        }
        .withRetry()
        .withRequestTimeout()
  }
  
  /**
   * Returns country by given [id]
   */
  fun getCountryById(id: String): Country {
    val countries = countries ?: throw IllegalStateException("Countries hasn't been saved yet")
    return countries.find { it.id == id } ?: throw IllegalStateException(
      "Cannot find country with id $id")
  }
  
  // Result cannot be used as a return type, so returning '() -> Result' instead.
  // See https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md#limitations for more info
  private fun mapToResult(
    resultInfo: Result<CountriesWrapper>,
    resultCases: Result<Map<String, Location>>
  ): () -> Result<Map<String, CountryOnMapMetaInfo>> {
    val countries = resultInfo.getOrElse { return { Result.failure(it) } }
    val locationsMap = resultCases.getOrElse { return { Result.failure(it) } }
    this.countries = countriesDomainMapper.map(countries.countries)
    return {
      val map = HashMap<String, CountryOnMapMetaInfo>()
      for (country in countries.countries) {
        val location = locationsMap[country.iso2] ?: continue
        map[country.iso2] = CountryOnMapMetaInfo(country.id, country.confirmed, location)
      }
      Result.success(map)
    }
  }
}