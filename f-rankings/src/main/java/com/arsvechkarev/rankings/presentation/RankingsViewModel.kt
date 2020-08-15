package com.arsvechkarev.rankings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.model.Country
import core.model.DisplayableCountry
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.exceptions.OnErrorNotImplementedException

class RankingsViewModel(
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : RxViewModel() {
  
  private val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  fun startInitialLoading() {
    updateFromNetwork()
  }
  
  fun updateFromNetwork() {
    if (connection.isNotConnected) {
      _state.value = Failure(Failure.FailureReason.NO_CONNECTION)
      return
    }
    
    rxCall {
      allCountriesRepository.getAllCountries()
          .subscribeOn(schedulersProvider.io())
          .map(::transformToBaseState)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .subscribe({
            _state.value = it
          }) {
            throw OnErrorNotImplementedException(it)
          }
    }
  }
  
  private fun transformToBaseState(countries: List<Country>): BaseScreenState {
    val displayableCountries = ArrayList<DisplayableCountry>()
    for (i in countries.indices) {
      val country = countries[i]
      displayableCountries.add(DisplayableCountry(country.name, country.confirmed))
    }
    displayableCountries.sortDescending()
    for (i in countries.indices) {
      displayableCountries[i].number = i + 1
    }
    return RankingsScreenState.Loaded(displayableCountries)
  }
  
}