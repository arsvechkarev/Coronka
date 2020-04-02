package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.NO_CONNECTION
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.TIMEOUT
import com.arsvechkarev.map.presentation.MapScreenState.Failure.FailureReason.UNKNOWN
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountries
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import core.Application
import core.Loggable
import core.NetworkConnection
import core.StateHandle
import core.doIfContains
import core.remove
import core.update
import core.updateAll
import datetime.PATTERN_STANDARD
import java.util.concurrent.TimeoutException

class MapViewModel(
  private val connection: NetworkConnection,
  private val threader: Application.Threader,
  private val interactor: CountriesInfoInteractor
) : ViewModel(), Loggable {
  
  override val logTag = "Map_MapViewModel"
  
  private val _state = MutableLiveData<StateHandle<MapScreenState>>(StateHandle())
  val state: LiveData<StateHandle<MapScreenState>>
    get() = _state
  
  fun startInitialLoading(allowUseSavedData: Boolean) {
    if (allowUseSavedData) {
      _state.updateAll()
      return
    }
    _state.update(LoadingCountries)
    interactor.tryUpdateFromCache { countries, dateTime ->
      _state.update(CountriesLoaded(countries, true, dateTime.formatted(PATTERN_STANDARD)))
    }
    loadFromNetwork(false)
  }
  
  fun loadFromNetwork(notifyShowLoading: Boolean = true) {
    if (notifyShowLoading) {
      _state.update(LoadingCountries)
    }
    if (connection.isNotConnected) {
      _state.update(Failure(NO_CONNECTION), remove = LoadingCountries::class)
      return
    }
    interactor.loadCountriesInfo(onSuccess = { countries, dateTime ->
      val state = CountriesLoaded(countries, false, dateTime.formatted(PATTERN_STANDARD))
      _state.update(state, remove = LoadingCountries::class)
    }, onFailure = {
      val failure = when (it) {
        is TimeoutException -> Failure(TIMEOUT)
        else -> Failure(UNKNOWN)
      }
      _state.update(failure, remove = LoadingCountries::class)
    })
  }
  
  fun findCountryByCode(countryCode: String) {
    _state.doIfContains(CountriesLoaded::class) {
      _state.update(LoadingCountryInfo)
      threader.backgroundWorker.submit {
        val country = countriesList.find { it.countryCode == countryCode } ?: return@submit
        _state.remove(LoadingCountryInfo::class)
        threader.mainThreadWorker.submit { _state.update(FoundCountry(country)) }
      }
    }
  }
  
  override fun onCleared() {
    interactor.removeListener()
  }
}