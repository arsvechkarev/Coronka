package com.arsvechkarev.common.executors

import com.arsvechkarev.common.TimedData
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.Loggable
import core.model.Country
import datetime.DateTime
import org.json.JSONObject

class CountriesInfoListenableExecutor(
  private val networker: Networker,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val saver: Saver
) : BaseListenableExecutor<TimedData<List<Country>>>(), Loggable {
  
  override val logTag = "CountriesInfoListenableExecutor"
  
  override fun performCacheRequest(): TimedData<List<Country>>? {
    if (sqLiteExecutor.isTableNotEmpty() && saver.has(COUNTRIES_INFO_LAST_UPDATE_TIME)) {
      val lastUpdateTime = saver.getString(COUNTRIES_INFO_LAST_UPDATE_TIME)
      val countries = sqLiteExecutor.getCountries()
      return TimedData(countries, DateTime.ofString(lastUpdateTime))
    }
    return null
  }
  
  override fun performNetworkRequest(): TimedData<List<Country>> {
    val json = networker.performRequest(URL)
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
    return TimedData(countriesList, DateTime.current())
  }
  
  override fun loadToCache(result: TimedData<List<Country>>) {
    threader.onIoThread {
      saver.execute(synchronously = true) {
        putString(COUNTRIES_INFO_LAST_UPDATE_TIME, DateTime.current().toString())
      }
    }
    threader.onIoThread {
      sqLiteExecutor.saveCountriesInfo(result.data)
    }
  }
  
  companion object {
    const val SAVER_FILENAME = "CountriesInfoListenableExecutor"
    private const val COUNTRIES_INFO_LAST_UPDATE_TIME = "countriesInfoLastUpdate"
    private const val URL = "https://api.covid19api.com/summary"
  }
}