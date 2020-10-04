package com.arsvechkarev.rankings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import core.NetworkConnection
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.db.CountriesMetaInfoDao
import core.db.CountriesMetaInfoTable
import core.model.OptionType
import core.model.TotalData
import core.model.WorldRegion
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.Observable
import io.reactivex.exceptions.OnErrorNotImplementedException

class RankingsViewModel(
  private val connection: NetworkConnection,
  private val allCountriesRepository: AllCountriesRepository,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  private var totalData: TotalData? = null
  private lateinit var countriesFilterer: CountriesFilterer
  
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
        countriesFilterer.filter(optionType, worldRegion)
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
    DatabaseManager.instance.readableDatabase.use { database ->
      val cursor = DatabaseExecutor.readAll(database, CountriesMetaInfoTable.TABLE_NAME)
      val elements = CountriesMetaInfoDao().getAll(cursor)
      countriesFilterer = CountriesFilterer(totalData.countries, elements)
      val worldRegion = WorldRegion.WORLDWIDE
      val optionType = OptionType.CONFIRMED
      val data = countriesFilterer.filter(optionType, worldRegion)
      return RankingsScreenState.Success(data, optionType, worldRegion)
    }
  }
}