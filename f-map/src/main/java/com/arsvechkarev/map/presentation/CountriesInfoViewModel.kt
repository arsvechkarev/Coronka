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
    interactor.updateCountriesInfo({ list ->
      list.forEach {
        println("qw: ${it.countryName}: ${it.countryId}|${it.countryName}|${it.confirmed}" +
            "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}")
      }
      _countriesData.setValue(CountriesInfoState.Success(list))
    })
  }
  
}