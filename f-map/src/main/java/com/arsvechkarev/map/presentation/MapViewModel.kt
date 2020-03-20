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
    repository.updateCountriesInfo {
      println("qwerty: set in viewmodel")
      _countriesData.postValue(CountriesInfoState.Success(it))
    }
  }
  
}