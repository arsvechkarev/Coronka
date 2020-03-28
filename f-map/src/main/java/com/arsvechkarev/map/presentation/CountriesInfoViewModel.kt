package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.NO_CONNECTION
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.StartLoadingCountries
import com.arsvechkarev.map.presentation.MapScreenState.StartLoadingCountryInfo
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import core.ApplicationConfig
import core.Loggable
import core.NetworkConnection
import core.NetworkViewModel
import core.StateHandle
import core.addOrUpdate
import core.doIfContains
import core.log
import core.remove
import core.updateAll

class CountriesInfoViewModel(
  connection: NetworkConnection,
  private val threader: ApplicationConfig.Threader,
  private val interactor: CountriesInfoInteractor
) : NetworkViewModel(connection), Loggable {
  
  override val logTag = "Map_NetworkViewModel"
  
  private val _stateHandle = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _stateHandle
  
  fun requestUpdateCountriesInfo(allowUseSavedData: Boolean) {
    if (allowUseSavedData) {
      _stateHandle.updateAll()
      return
    }
    _stateHandle.addOrUpdate(StartLoadingCountries)
    log { "isConnected = ${connection.isConnected}" }
    if (connection.isNotConnected) {
      _stateHandle.addOrUpdate(Failure(NO_CONNECTION), remove = StartLoadingCountries::class)
      return
    }
    threader.backgroundWorker.submit {
      interactor.updateCountriesInfo(onSuccess = { list ->
        _stateHandle.addOrUpdate(CountriesLoaded(list), remove = StartLoadingCountries::class)
      })
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    _stateHandle.doIfContains(CountriesLoaded::class) {
      _stateHandle.addOrUpdate(StartLoadingCountryInfo)
      threader.backgroundWorker.submit {
        val country = countriesList.find { it.countryCode == countryCode } ?: return@submit
        _stateHandle.remove(StartLoadingCountryInfo::class)
        threader.mainThreadWorker.submit { _stateHandle.addOrUpdate(FoundCountry(country)) }
      }
    }
  }
}