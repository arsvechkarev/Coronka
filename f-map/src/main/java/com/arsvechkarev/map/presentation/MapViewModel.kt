package com.arsvechkarev.map.presentation

import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.RxViewModel
import core.Schedulers
import core.datasources.CountriesMetaInfoDataSource
import core.datasources.TotalInfoDataSource
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.extenstions.withRetry
import core.model.Country
import core.model.CountryOnMap
import core.model.Location
import core.model.TotalInfo
import io.reactivex.Observable

class MapViewModel(
  private val totalInfoDataSource: TotalInfoDataSource,
  private val countriesMetaInfoDataSource: CountriesMetaInfoDataSource,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
    rxCall {
      Observable.zip(
        totalInfoDataSource.requestTotalInfo().subscribeOn(schedulers.io()),
        countriesMetaInfoDataSource.getLocationsMap().subscribeOn(schedulers.io()),
        { map, countries -> Pair(map, countries) }
      ).withNetworkDelay(schedulers)
          .withRequestTimeout()
          .map(::transformResult)
          .withRetry()
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
  
  private fun transformResult(pair: Pair<TotalInfo, Map<String, Location>>): BaseScreenState {
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
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}