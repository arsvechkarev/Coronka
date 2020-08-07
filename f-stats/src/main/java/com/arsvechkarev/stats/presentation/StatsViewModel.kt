package com.arsvechkarev.stats.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.stats.domain.ListFilterer
import com.arsvechkarev.stats.presentation.StatsScreenState.FilteredCountries
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromCache
import com.arsvechkarev.stats.presentation.StatsScreenState.LoadedFromNetwork
import com.arsvechkarev.stats.presentation.StatsScreenState.Loading
import core.Loggable
import core.NetworkConnection
import core.RxViewModel
import core.SavedData
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.model.Country
import core.model.GeneralInfo
import core.model.OptionType
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.StateHandle
import core.state.currentValue
import core.state.update
import core.state.updateSelf
import io.reactivex.Single
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.functions.BiFunction

class StatsViewModel(
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val generalInfoRepository: GeneralInfoRepository,
  private val filterer: ListFilterer,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : RxViewModel(), Loggable {
  
  override val logTag = "Base_Stats_ViewModel"
  
  private val savedData = SavedData()
  
  private val _state = MutableLiveData<StateHandle<BaseScreenState>>(StateHandle())
  val state: LiveData<StateHandle<BaseScreenState>>
    get() = _state
  
  fun startInitialLoading(isRecreated: Boolean) {
    if (isRecreated) {
      _state.updateSelf(isRecreated = true)
      return
    }
    updateFromNetwork(notifyLoading = false)
  }
  
  fun updateFromNetwork(notifyLoading: Boolean = true) {
    if (connection.isNotConnected) {
      _state.update(Failure(NO_CONNECTION))
      return
    }
    
    rxCall {
      allCountriesRepository.getAllCountries()
          .subscribeOn(schedulersProvider.io())
          .zipWith(generalInfoRepository.getGeneralInfo()
              .subscribeOn(schedulersProvider.io()), BiFunction(::convertToPair)
          )
          .map(::filterResult)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .subscribe(_state::update) {
            throw OnErrorNotImplementedException(it)
          }
    }
  }
  
  fun filterList(optionType: OptionType) {
    when (_state.currentValue) {
      is LoadedFromCache -> performFiltering(optionType)
      is LoadedFromNetwork -> performFiltering(optionType)
      is FilteredCountries -> performFiltering(optionType)
    }
  }
  
  private fun convertToPair(
    countries: List<Country>,
    generalInfo: GeneralInfo
  ): Pair<List<Country>, GeneralInfo> {
    savedData.add(countries)
    savedData.add(generalInfo)
    return Pair(countries, generalInfo)
  }
  
  private fun filterResult(pair: Pair<List<Country>, GeneralInfo>): BaseScreenState {
    return LoadedFromNetwork(filterer.filter(pair.first, pair.second, OptionType.CONFIRMED))
  }
  
  private fun performFiltering(optionType: OptionType) {
    rxCall {
      val filteredList = filterer.filter(savedData.get(), savedData.get(), optionType)
      Single.just(filteredList)
          .map { list -> FilteredCountries(list) }
          .subscribe(_state::update)
    }
  }
}