package com.arsvechkarev.map.presentation

import base.RxViewModel
import com.arsvechkarev.map.domain.MapInteractor
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.Schedulers

class MapViewModel(
  private val mapInteractor: MapInteractor,
  private val networkAvailabilityNotifier: NetworkAvailabilityNotifier,
  private val schedulers: Schedulers
) : RxViewModel(), NetworkListener {
  
  init {
    networkAvailabilityNotifier.registerListener(this)
  }
  
  override fun onNetworkAvailable() {
    if (_state.value is Failure) {
      schedulers.mainThread().scheduleDirect(::retryLoadingData)
    }
  }
  
  fun startLoadingData() {
    if (state.value != null) return
    performLoadingData()
  }
  
  fun retryLoadingData() {
    if (state.value !is Failure) return
    performLoadingData()
  }
  
  private fun performLoadingData() {
    rxCall {
      mapInteractor.requestCountriesMap()
          .map<BaseScreenState>(::LoadedCountries)
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun showCountryInfo(countryId: String) {
    val country = mapInteractor.getCountryById(countryId)
    when (val state = _state.value) {
      is LoadedCountries -> _state.value = FoundCountry(state.iso2ToCountryMapMetaInfo, country)
      is FoundCountry -> _state.value = FoundCountry(state.iso2ToCountryMapMetaInfo, country)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
}