package com.arsvechkarev.map.presentation

import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CountriesMetaInfoRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.model.Country
import core.model.CountryOnMap
import core.model.Location
import core.model.TotalData
import io.reactivex.Observable

class MapViewModel(
  private val allCountriesRepository: AllCountriesRepository,
  private val countriesMetaInfoRepository: CountriesMetaInfoRepository,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  fun startLoadingData() {
    rxCall {
      Observable.zip(
        allCountriesRepository.getTotalData().subscribeOn(schedulers.io()),
        countriesMetaInfoRepository.getLocationsMap().subscribeOn(schedulers.io()),
        { map, countries -> Pair(map, countries) }
      ).withNetworkDelay(schedulers)
          .withRequestTimeout()
          .map(::transformResult)
          .onErrorReturn(::Failure)
          .startWith(Loading())
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun showCountryInfo(country: Country) {
    when (val state = _state.value) {
      is LoadedCountries -> notifyFoundCountry(state.iso2ToCountryMap, country)
      is FoundCountry -> notifyFoundCountry(state.iso2ToCountryMap, country)
    }
  }
  
  private fun transformResult(pair: Pair<TotalData, Map<String, Location>>): BaseScreenState {
    val map = HashMap<String, CountryOnMap>()
    for (country in pair.first.countries) {
      val location = pair.second[country.iso2] ?: continue
      map[country.iso2] = CountryOnMap(country, location)
    }
    return LoadedCountries(map)
  }
  
  private fun notifyFoundCountry(
    iso2ToLocations: Map<String, CountryOnMap>,
    country: Country
  ) {
    _state.value = FoundCountry(iso2ToLocations, country)
  }
}