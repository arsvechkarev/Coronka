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
import core.Schedulers
import core.model.OptionType
import core.model.WorldRegion
import core.model.ui.DisplayableCountry

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
      schedulers.mainThread().scheduleDirect(::startLoadingData)
    }
  }
  
  fun startLoadingData() {
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
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
  
  companion object {
    
    val DefaultWorldRegion = WorldRegion.WORLDWIDE
    val DefaultOptionType = OptionType.CONFIRMED
  }
}