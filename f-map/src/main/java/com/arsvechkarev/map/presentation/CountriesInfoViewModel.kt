package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.ShowingCountryInfo
import com.arsvechkarev.map.repository.CountriesInfoFacade
import core.ApplicationConfig
import core.extenstions.updateSelf
import core.model.Country

class CountriesInfoViewModel(
  private val threader: ApplicationConfig.Threader,
  private val facade: CountriesInfoFacade
) : ViewModel() {
  
  private val _state = MutableLiveData<MapScreenState>()
  val state: LiveData<MapScreenState>
    get() = _state
  
  fun requestUpdateCountriesInfo(allowUseSavedData: Boolean) {
    if (allowUseSavedData) {
      tryToUpdateWithSavedData()
      return
    }
    threader.backgroundWorker.submit {
      facade.updateCountriesInfo({ list ->
        _state.value = CountriesLoaded(list)
      })
    }
  }
  
  fun findCountryByCode(countryCode: String) {
    when (val state: MapScreenState? = _state.value) {
      is CountriesLoaded -> notifyViewModelIfFound(state.countriesList, countryCode)
      is ShowingCountryInfo -> notifyViewModelIfFound(state.countriesList, countryCode)
    }
  }
  
  private fun tryToUpdateWithSavedData() {
    when (_state.value) {
      is CountriesLoaded -> _state.updateSelf()
      is ShowingCountryInfo -> _state.updateSelf()
    }
  }
  
  private fun notifyViewModelIfFound(countriesList: List<Country>, countryCode: String) {
    val country: Country? = countriesList.find { it.countryCode == countryCode }
    if (country != null) {
      _state.value = ShowingCountryInfo(country, countriesList)
    }
  }
  
}