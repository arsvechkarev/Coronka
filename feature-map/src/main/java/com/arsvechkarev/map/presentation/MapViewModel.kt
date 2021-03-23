package com.arsvechkarev.map.presentation

import base.RxViewModel
import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.common.domain.CountriesMetaInfoRepository
import com.arsvechkarev.common.domain.TotalInfoDataSource
import com.arsvechkarev.map.utils.MapTransformer
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.Schedulers
import core.model.Country
import core.model.CountryOnMap
import core.model.Location
import core.model.TotalInfo
import io.reactivex.Observable
import io.reactivex.Single

class MapViewModel(
  private val totalInfoDataSource: TotalInfoDataSource,
  private val countriesMetaInfoRepository: CountriesMetaInfoRepository,
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
      Single.zip(
        totalInfoDataSource.requestTotalInfo().subscribeOn(schedulers.io())
            .map(Result.Companion::success)
            .onErrorReturn(Result.Companion::failure),
        countriesMetaInfoRepository.getLocationsMap().subscribeOn(schedulers.io())
            .map(Result.Companion::success)
            .onErrorReturn(Result.Companion::failure),
        { map, countries -> mapToScreenState(map, countries) }
      ).toObservable()
          .withNetworkDelay(schedulers)
          .flatMap { item ->
            when (item) {
              is Failure -> Observable.error(item.throwable)
              else -> Observable.just(item)
            }
          }
          .withRetry()
          .withRequestTimeout()
          .onErrorReturn(::Failure)
          .startWith(Loading)
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
  
  private fun mapToScreenState(
    resultTotalInfo: Result<TotalInfo>,
    resultCountries: Result<Map<String, Location>>
  ): BaseScreenState {
    val totalInfo = resultTotalInfo.getOrElse { return Failure(it) }
    val locationsMap = resultCountries.getOrElse { return Failure(it) }
    return LoadedCountries(MapTransformer.transformResult(totalInfo, locationsMap))
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