package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CountriesMetaInfoRepository
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.model.OptionType
import core.model.TotalData
import core.model.WorldRegion
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class RankingsViewModel(
  private val allCountriesRepository: AllCountriesRepository,
  private val metaInfoRepository: CountriesMetaInfoRepository,
  private val schedulers: Schedulers = AndroidSchedulers,
  private val delayMilliseconds: Long = 1000
) : RxViewModel() {
  
  private lateinit var countriesFilterer: CountriesFilterer
  
  fun startLoadingData() {
    rxCall {
      allCountriesRepository.getData()
          .subscribeOn(schedulers.io())
          .delay(delayMilliseconds, TimeUnit.MILLISECONDS, schedulers.computation(), true)
          .map(::transformToScreenState)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .observeOn(schedulers.mainThread())
          .subscribe(_state::setValue)
    }
  }
  
  fun filter(optionType: OptionType, worldRegion: WorldRegion) {
    rxCall {
      Observable.fromCallable {
        countriesFilterer.filter(optionType, worldRegion)
      }
          .subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .subscribe { list ->
            _state.value = FilteredCountries(list)
          }
    }
  }
  
  private fun transformToScreenState(totalData: TotalData): BaseScreenState {
    val countriesMetaInfo = metaInfoRepository.getCountriesMetaInfoSync()
    countriesFilterer = CountriesFilterer(totalData.countries, countriesMetaInfo)
    val worldRegion = WorldRegion.WORLDWIDE
    val optionType = OptionType.CONFIRMED
    val data = countriesFilterer.filter(optionType, worldRegion)
    return LoadedCountries(data, optionType, worldRegion)
  }
}