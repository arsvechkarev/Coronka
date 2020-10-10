package com.arsvechkarev.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CountriesMetaInfoRepository
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.Loaded
import core.RxViewModel
import core.concurrency.AndroidSchedulers
import core.concurrency.Schedulers
import core.model.Country
import core.model.CountryOnMap
import core.model.Location
import core.model.TotalData
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.Companion.asFailureReason
import core.state.Loading
import io.reactivex.Observable

class MapViewModel(
  private val allCountriesRepository: AllCountriesRepository,
  private val countriesMetaInfoRepository: CountriesMetaInfoRepository,
  private val schedulers: Schedulers = AndroidSchedulers
) : RxViewModel() {
  
  private val _state = MutableLiveData<BaseScreenState>()
  val state: LiveData<BaseScreenState>
    get() = _state
  
  fun startLoadingData() {
    rxCall {
      Observable.zip(
        allCountriesRepository.getData().subscribeOn(schedulers.io()),
        countriesMetaInfoRepository.getLocationsMap().subscribeOn(schedulers.io()),
        { map, countries -> Pair(map, countries) }
      ).observeOn(schedulers.mainThread())
          .map(::transformResult)
          .onErrorReturn { Failure(it.asFailureReason()) }
          .startWith(Loading)
          .subscribe(_state::setValue)
    }
  }
  
  fun showCountryInfo(country: Country) {
    when (val state = _state.value) {
      is Loaded -> notifyFoundCountry(state.iso2ToCountryMap, country)
      is FoundCountry -> notifyFoundCountry(state.iso2ToCountryMap, country)
    }
  }
  
  private fun transformResult(pair: Pair<TotalData, Map<String, Location>>): BaseScreenState {
    val map = HashMap<String, CountryOnMap>()
    for (country in pair.first.countries) {
      val location = pair.second[country.iso2] ?: continue
      map[country.iso2] = CountryOnMap(country, location)
    }
    return Loaded(map)
  }
  
  private fun notifyFoundCountry(
    iso2ToLocations: Map<String, CountryOnMap>,
    country: Country
  ) {
    _state.value = FoundCountry(iso2ToLocations, country)
  }
}