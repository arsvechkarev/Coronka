package com.arsvechkarev.rankings.presentation

import api.recycler.DifferentiableItem
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
import core.markAsOld
import core.model.OptionType
import core.model.WorldRegion
import core.model.ui.CountryFullInfo
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
  
  fun onCountryClicked(country: DisplayableCountry) {
    val currentState = state.value
    require(currentState is Success)
    if (currentState.showFilterDialog || currentState.countryFullInfo != null) {
      return
    }
    rxCall {
      rankingsInteractor.getCountryFullInfo(country)
          .smartSubscribe { countryFullInfo ->
            updateSuccessState(showFilterDialog = false, countryFullInfo = countryFullInfo)
          }
    }
  }
  
  fun onFilterDialogShow() {
    if (currentSuccessState().showFilterDialog) return
    updateSuccessState(showFilterDialog = true)
  }
  
  fun onFilterDialogHide() {
    if (!currentSuccessState().showFilterDialog) return
    updateSuccessState(showFilterDialog = false)
  }
  
  fun onCountryFullInfoDialogBackClicked() {
    updateSuccessState(countryFullInfo = null)
  }
  
  fun onNewOptionTypeSelected(optionType: OptionType) {
    filter(currentSuccessState().worldRegion, optionType)
  }
  
  fun onNewWorldRegionSelected(worldRegion: WorldRegion) {
    filter(worldRegion, currentSuccessState().optionType)
  }
  
  fun onDestroy() {
    _state.markAsOld()
  }
  
  fun allowBackPress(): Boolean {
    val currentState = state.value
    if (currentState !is Success) return true
    if (currentState.countryFullInfo != null) {
      updateSuccessState(countryFullInfo = null)
      return false
    }
    if (currentState.showFilterDialog) {
      updateSuccessState(showFilterDialog = false)
      return false
    }
    return true
  }
  
  private fun performLoadingData() {
    rxCall {
      rankingsInteractor.requestCountries(DefaultWorldRegion, DefaultOptionType)
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRetry()
          .withRequestTimeout()
          .map<BaseScreenState> { countries ->
            Success(
              countries = countries,
              isListChanged = true,
              worldRegion = DefaultWorldRegion,
              optionType = DefaultOptionType,
              showFilterDialog = false,
              countryFullInfo = null
            )
          }
          .onErrorReturn(::Failure)
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  private fun filter(worldRegion: WorldRegion, optionType: OptionType) {
    require(state.value is Success)
    rxCall {
      rankingsInteractor.filterCountries(worldRegion, optionType)
          .subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .smartSubscribe { list ->
            updateSuccessState(
              countries = list, isListChanged = true,
              worldRegion = worldRegion, optionType = optionType
            )
          }
    }
  }
  
  private fun updateSuccessState(
    countries: List<DifferentiableItem> = currentSuccessState().countries,
    isListChanged: Boolean = false,
    worldRegion: WorldRegion = currentSuccessState().worldRegion,
    optionType: OptionType = currentSuccessState().optionType,
    showFilterDialog: Boolean = currentSuccessState().showFilterDialog,
    countryFullInfo: CountryFullInfo? = currentSuccessState().countryFullInfo,
  ) {
    _state.value = Success(countries, isListChanged, worldRegion, optionType,
      showFilterDialog, countryFullInfo)
  }
  
  private fun currentSuccessState() = state.value as Success
  
  override fun onCleared() {
    networkAvailabilityNotifier.unregisterListener(this)
  }
  
  companion object {
    
    val DefaultWorldRegion = WorldRegion.WORLDWIDE
    val DefaultOptionType = OptionType.CONFIRMED
  }
}