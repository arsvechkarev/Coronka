package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CountriesMetaInfoRepository
import core.BaseScreenState
import core.Failure
import core.Loading
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.extenstions.withNetworkDelay
import core.extenstions.withRequestTimeout
import core.model.OptionType
import core.model.TotalData
import core.model.WorldRegion
import io.reactivex.Observable

class RankingsViewModel(
  private val allCountriesRepository: AllCountriesRepository,
  private val metaInfoRepository: CountriesMetaInfoRepository,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  private lateinit var countriesFilterer: CountriesFilterer
  
  fun startLoadingData() {
    rxCall {
      allCountriesRepository.getData()
          .subscribeOn(schedulers.io())
          .withNetworkDelay(schedulers)
          .withRequestTimeout()
          .map(::transformToScreenState)
          .onErrorReturn(::Failure)
          .startWith(Loading())
          .observeOn(schedulers.mainThread())
          .smartSubscribe(_state::setValue)
    }
  }
  
  fun filter(optionType: OptionType, worldRegion: WorldRegion) {
    rxCall {
      Observable.fromCallable {
        countriesFilterer.filter(optionType, worldRegion)
      }
          .subscribeOn(schedulers.computation())
          .observeOn(schedulers.mainThread())
          .smartSubscribe { list -> _state.value = FilteredCountries(list) }
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