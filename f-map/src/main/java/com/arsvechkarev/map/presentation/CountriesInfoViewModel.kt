package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.NO_CONNECTION
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.TIMEOUT
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountries
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import core.ApplicationConfig
import core.Loggable
import core.NetworkConnection
import core.StateHandle
import core.addOrUpdate
import core.doIfContains
import core.log
import core.remove
import core.updateAll
import java.util.concurrent.TimeoutException

class CountriesInfoViewModel(
  private val connection: NetworkConnection,
  private val threader: ApplicationConfig.Threader,
  private val interactor: CountriesInfoInteractor
) : ViewModel(), Loggable {
  
  override val logTag = "Map_NetworkViewModel"
  
  private val _state = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _state
  
  fun requestUpdateCountriesInfo(allowUseSavedData: Boolean) {
    if (allowUseSavedData) {
      _state.updateAll()
      return
    }
    log { "before" }
    _state.addOrUpdate(LoadingCountries)
    log { "isConnected = ${connection.isConnected}" }
    interactor.tryUpdateFromCache {
      log { "countries loaded" }
      _state.addOrUpdate(CountriesLoaded(it, true))
    }
    if (connection.isNotConnected) {
      _state.addOrUpdate(Failure(NO_CONNECTION), remove = LoadingCountries::class)
      return
    }
    interactor.updateCountriesInfo(onSuccess = { list ->
      _state.addOrUpdate(CountriesLoaded(list, false), remove = LoadingCountries::class)
    }, onFailure = {
      if (it is TimeoutException) {
        log { "printingTimeout" }
        _state.addOrUpdate(Failure(TIMEOUT), remove = LoadingCountries::class)
      }
    })
  }
  
  fun findCountryByCode(countryCode: String) {
    _state.doIfContains(CountriesLoaded::class) {
      _state.addOrUpdate(LoadingCountryInfo)
      threader.backgroundWorker.submit {
        val country = countriesList.find { it.countryCode == countryCode } ?: return@submit
        _state.remove(LoadingCountryInfo::class)
        threader.mainThreadWorker.submit { _state.addOrUpdate(FoundCountry(country)) }
      }
    }
  }
  
  override fun onCleared() {
    interactor.removeListener()
  }
}