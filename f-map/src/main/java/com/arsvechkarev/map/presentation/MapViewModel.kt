package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.Loaded
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.AndroidThreader
import core.concurrency.Schedulers
import core.concurrency.Threader
import core.model.Country
import core.model.TotalData
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading

class MapViewModel(
  private val allCountriesRepository: AllCountriesRepository,
  private val threader: Threader = AndroidThreader,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  private val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  fun startLoadingData() {
    rxCall {
      allCountriesRepository.getData()
          .subscribeOn(schedulers.io())
          .map(::transformResult)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .subscribe(_state::setValue)
    }
  }
  
  fun showCountryInfo(country: Country) {
    when (val currentState = _state.value) {
      is Loaded -> notifyFoundCountry(currentState.countries, country)
      is FoundCountry -> notifyFoundCountry(currentState.countries, country)
    }
  }
  
  private fun transformResult(totalData: TotalData): BaseScreenState {
    return Loaded(totalData.countries)
  }
  
  private fun notifyFoundCountry(countries: List<Country>, foundCountry: Country) {
    threader.onMainThread { _state.value = FoundCountry(countries, foundCountry) }
  }
}