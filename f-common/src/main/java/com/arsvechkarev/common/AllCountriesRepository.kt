package com.arsvechkarev.common

import com.arsvechkarev.network.RxNetworker
import com.arsvechkarev.storage.Saver
import core.Loggable
import core.MAX_CACHE_MINUTES
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.log
import core.model.Country
import core.state.BaseScreenState
import io.reactivex.Observable
import org.json.JSONObject

class AllCountriesRepository(
  private val networker: RxNetworker,
  private val saver: Saver,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : Loggable {
  
  override val logTag = "Request_AllCountriesRepository"
  private var observable: Observable<List<Country>>? = null
  
  fun getAllCountries(): Observable<List<Country>> {
    if (observable == null) {
      observable = createLoadingObservable()
    }
    return observable!!
  }
  
  private fun createLoadingObservable(): Observable<List<Country>> {
    return Observable.concat(getFromCache(), getFromNetwork())
        .subscribeOn(schedulersProvider.io())
        .firstElement()
        .toObservable()
        .share()
        .observeOn(schedulersProvider.mainThread())
  }
  
  private fun getFromCache(): Observable<List<Country>> = Observable.create { emitter ->
    val isUpToDate = saver.isUpToDate(COUNTRIES_LAST_UPDATE_TIME, MAX_CACHE_MINUTES)
    if (isUpToDate && sqLiteExecutor.isTableNotEmpty()) {
      emitter.onNext(sqLiteExecutor.getCountries())
      log { "Countries info found in cache" }
    }
    emitter.onComplete()
  }
  
  // TODO (6/13/2020): Add cache
  private fun getFromNetwork(): Observable<List<Country>> {
    log { "getting countries" }
    return networker.requestObservable(URL)
        .map { transformJson(it) }
  }
  
  private fun transformJson(json: String): List<Country> {
    val countriesList = ArrayList<Country>()
    val jsonObject = JSONObject(json)
    val jsonArray = jsonObject.getJSONArray("Countries")
    for (i in 0 until jsonArray.length()) {
      val item = jsonArray.get(i) as JSONObject
      val country = Country(
        id = i,
        name = item.getString("Country"),
        slug = item.getString("Slug"),
        iso2 = item.getString("CountryCode"),
        confirmed = item.getString("TotalConfirmed").toInt(),
        deaths = item.getString("TotalDeaths").toInt(),
        recovered = item.getString("TotalRecovered").toInt()
      )
      countriesList.add(country)
    }
    return countriesList
  }
  
  companion object {
    
    const val SAVER_FILENAME = "AllCountriesRepository"
    
    private const val COUNTRIES_LAST_UPDATE_TIME = "countriesLastUpdateTime"
    private const val URL = "https://api.covid19api.com/summary"
  }
}