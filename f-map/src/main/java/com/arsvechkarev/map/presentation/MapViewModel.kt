package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromCache
import com.arsvechkarev.map.presentation.MapScreenState.LoadedFromNetwork
import core.Loggable
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.concurrency.Threader
import core.extenstions.startWithIf
import core.model.Country
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.Loading
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf

class MapViewModel(
  private val threader: Threader,
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : RxViewModel(), Loggable {
  
  override val logTag = "Base_Map_ViewModel"
  
  private val _state = MutableLiveData<StateHandle<BaseScreenState>>(StateHandle())
  val state: LiveData<StateHandle<BaseScreenState>>
    get() = _state
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateSelf(isRecreated = true)
      return
    }
    updateFromNetwork()
  }
  
  fun updateFromNetwork() {
    rxCall {
      allCountriesRepository.getAllCountries()
          .subscribeOn(schedulersProvider.io())
          .map(::transformResult)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .observeOn(schedulersProvider.mainThread())
          .startWith(Loading)
          .startWithIf(Failure(NO_CONNECTION), connection.isNotConnected)
          .subscribe(_state::update)
    }
  }
  
  fun showCountryInfo(country: Country) {
    when (val currentState = _state.currentValue) {
      is LoadedFromCache -> notifyFoundCountry(currentState.countries, country)
      is LoadedFromNetwork -> notifyFoundCountry(currentState.countries, country)
      is FoundCountry -> notifyFoundCountry(currentState.countries, country)
    }
  }
  
  private fun transformResult(countries: List<Country>): BaseScreenState {
    return LoadedFromNetwork(countries)
  }
  
  private fun notifyFoundCountry(
    countries: List<Country>, foundCountry: Country
  ) {
    threader.onMainThread { _state.update(FoundCountry(countries, foundCountry)) }
  }
}