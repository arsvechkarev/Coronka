package com.arsvechkarev.rankings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.rankings.list.HeaderItemAdapterDelegate
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.extenstions.assertThat
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.TotalData
import core.model.WorldRegion
import core.recycler.SortableDisplayableItem
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.Observable
import io.reactivex.exceptions.OnErrorNotImplementedException

class RankingsViewModel(
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val listFilterer: ListFilterer,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  private var totalData: TotalData? = null
  
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
      allCountriesRepository.getData()
          .subscribeOn(schedulers.io())
          .map(::transformToScreenState)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .subscribe({
            _state.value = it
          }) {
            throw OnErrorNotImplementedException(it)
          }
    }
  }
  
  fun filter(optionType: OptionType, worldRegion: WorldRegion) {
    rxCall {
      Observable.fromCallable {
        listFilterer.filter(totalData!!.countries, totalData!!.generalInfo, optionType, worldRegion)
      }
          .subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .subscribe({
            _state.value = RankingsScreenState.Success(it, optionType, worldRegion)
          }, {
            throw Exception(it)
          })
    }
  }
  
  private fun transformToScreenState(totalData: TotalData): BaseScreenState {
    this.totalData = totalData
    val countries = totalData.countries
    val displayableItems = ArrayList<SortableDisplayableItem>(countries.size + 1)
    displayableItems.add(HeaderItemAdapterDelegate.Header)
    for (i in countries.indices) {
      val country = countries[i]
      displayableItems.add(DisplayableCountry(country.name, country.confirmed))
    }
    displayableItems.sortWith(Comparator { item1, item2 ->
      if (item1 is HeaderItemAdapterDelegate.Header) return@Comparator -1
      if (item2 is HeaderItemAdapterDelegate.Header) return@Comparator 1
      assertThat(item1 is DisplayableCountry && item2 is DisplayableCountry)
      return@Comparator item2.compareTo(item1)
    })
    for (i in 1 until displayableItems.size) {
      (displayableItems[i] as DisplayableCountry).number = i
    }
    return RankingsScreenState.Success(displayableItems, OptionType.CONFIRMED, WorldRegion.WORLDWIDE)
  }
}