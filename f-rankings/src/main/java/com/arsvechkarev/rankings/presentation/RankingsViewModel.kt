package com.arsvechkarev.rankings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.rankings.list.HeaderItemAdapterDelegate
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.extenstions.assertThat
import core.model.Country
import core.model.DisplayableCountry
import core.recycler.SortableDisplayableItem
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
          .observeOn(schedulersProvider.mainThread())
          .subscribe({
            _state.value = it
          }) {
            throw OnErrorNotImplementedException(it)
          }
    }
  }
  
  private fun transformToBaseState(countries: List<Country>): BaseScreenState {
    val displayableItems = ArrayList<SortableDisplayableItem>(countries.size + 1)
    displayableItems.add(HeaderItemAdapterDelegate.Header2)
    for (i in countries.indices) {
      val country = countries[i]
      displayableItems.add(DisplayableCountry(country.name, country.confirmed))
    }
    displayableItems.sortWith(Comparator { item1, item2 ->
      if (item1 is HeaderItemAdapterDelegate.Header2) return@Comparator -1
      if (item2 is HeaderItemAdapterDelegate.Header2) return@Comparator 1
      assertThat(item1 is DisplayableCountry && item2 is DisplayableCountry)
      return@Comparator item2.compareTo(item1)
    })
    for (i in 1 until displayableItems.size) {
      (displayableItems[i] as DisplayableCountry).number = i
    }
    return RankingsScreenState.Success(displayableItems)
  }
}