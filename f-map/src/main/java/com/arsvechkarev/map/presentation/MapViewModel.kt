package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.map.repository.MapRepository

class MapViewModel(private val repository: MapRepository) : ViewModel() {
  
  private val _countriesData = MutableLiveData<CountriesInfoState>()
  val countriesData: LiveData<CountriesInfoState>
    get() = _countriesData
  
  fun requestUpdateCountriesInfo() {
    repository.updateCountriesInfo { list ->
      println("qwerty: set in viewmodel")
      list.forEach {
        println("qw: ${it.countryName}: ${it.countryId}|${it.countryName}|${it.confirmed}" +
            "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}")
      }
      _countriesData.postValue(CountriesInfoState.Success(list))
    }
  }
  
}