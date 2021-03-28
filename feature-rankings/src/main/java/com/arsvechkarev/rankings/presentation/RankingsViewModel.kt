package com.arsvechkarev.rankings.presentation

import base.RxViewModel
import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.rankings.domain.RankingsInteractor
import core.BaseScreenState
import core.Failure
import core.Loading
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.model.OptionType
import core.model.WorldRegion
import core.model.ui.DisplayableCountry
import core.rx.Schedulers

class RankingsViewModel(
  private val rankingsInteractor: RankingsInteractor,
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
  
  fun filter(worldRegion: WorldRegion, optionType: OptionType) {
    rxCall {
      rankingsInteractor.filterCountries(worldRegion, optionType)
          .subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .smartSubscribe { list -> _state.value = FilteredCountries(list) }
    }
  }
  
  fun onCountryClicked(country: DisplayableCountry) {
    rxCall {
      rankingsInteractor.getCountryFullInfo(country)
          .subscribe { countryFullInfo ->
            _state.value = ShowCountryInfo(countryFullInfo)
          }
    }
  }
  
  private fun performLoadingData() {
    rxCall {
      rankingsInteractor.requestCountries(DefaultWorldRegion, DefaultOptionType)
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState>(::LoadedCountries)
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
  
  companion object {
    
    val DefaultWorldRegion = WorldRegion.WORLDWIDE
    val DefaultOptionType = OptionType.CONFIRMED
  }
}