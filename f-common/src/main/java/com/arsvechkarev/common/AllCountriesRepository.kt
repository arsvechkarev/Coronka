package com.arsvechkarev.common

import com.arsvechkarev.network.RxNetworker
import com.arsvechkarev.storage.Saver
import core.Loggable
import core.concurrency.AndroidSchedulersProvider
import core.concurrency.SchedulersProvider
import core.log
import core.model.Country
import io.reactivex.Maybe
import io.reactivex.Single
import org.json.JSONObject

class AllCountriesRepository(
  private val networker: RxNetworker,
  private val saver: Saver,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val schedulersProvider: SchedulersProvider = AndroidSchedulersProvider
) : Loggable {
  
  override val logTag = "Request_AllCountriesRepository"
  
  fun getAllCountries(): Single<List<Country>> {
    return Maybe.concat(getFromCache(), getFromNetwork())
        .firstElement()
        .toSingle()
  }
  
  private fun getFromCache(): Maybe<List<Country>> = Maybe.create { emitter ->
    val isUpToDate = saver.isUpToDate(COUNTRIES_LAST_UPDATE_TIME, MAX_CACHE_MINUTES)
    if (isUpToDate && sqLiteExecutor.isTableNotEmpty()) {
      emitter.onSuccess(sqLiteExecutor.getCountries())
      log { "Countries info found in cache" }
    } else {
      emitter.onComplete()
      log { "Countries not found in cache or is out of date" }
    }
  }
  
  private fun getFromNetwork(): Maybe<List<Country>> {
    return networker.performRequest(URL)
        .subscribeOn(schedulersProvider.io())
        .map(::transformJson)
        .toMaybe()
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