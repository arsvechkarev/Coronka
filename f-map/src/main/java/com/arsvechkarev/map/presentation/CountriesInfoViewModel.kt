package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.map.repository.CountriesInfoInteractor

class CountriesInfoViewModel(private val interactor: CountriesInfoInteractor) : ViewModel() {
  
  private val _countriesData = MutableLiveData<CountriesInfoState>()
  val countriesData: LiveData<CountriesInfoState>
    get() = _countriesData
  
  fun requestUpdateCountriesInfo() {
    if (_countriesData.value is CountriesInfoState.Success) {
      _countriesData.value = _countriesData.value
      return
    }
    interactor.updateCountriesInfo({ list ->
      _countriesData.value = CountriesInfoState.Success(list)
    })
  }
  
}