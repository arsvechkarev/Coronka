package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.ShowingCountryInfo
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import core.ApplicationConfig
import core.model.Country

class CountriesInfoViewModel(
  private val threader: ApplicationConfig.Threader,
  private val interactor: CountriesInfoInteractor
) : ViewModel() {
  
  private val _state = MutableLiveData<MapScreenState>()
  val state: LiveData<MapScreenState>
    get() = _state
  
  fun requestUpdateCountriesInfo(allowUseCache: Boolean) {
    if (allowUseCache && _state.value is CountriesLoaded) {
      _state.value = _state.value
      return
    }
    threader.backgroundWorker.submit {
      interactor.updateCountriesInfo({ list ->
        _state.value = CountriesLoaded(list)
      })
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    when (val state = _state.value) {
      is CountriesLoaded -> {
        notifyViewModelIfFound(state.countriesList, countryCode)
      }
      is ShowingCountryInfo -> {
        notifyViewModelIfFound(state.countriesList, countryCode)
      }
    }
  }
  
  private fun notifyViewModelIfFound(countriesList: List<Country>, countryCode: String) {
    val country = countriesList.find { it.countryCode == countryCode }
    if (country != null) {
      _state.value = ShowingCountryInfo(country, countriesList)
    }
  }
  
}