package com.arsvechkarev.common.executors

import com.arsvechkarev.common.TimedData
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.Loggable
import core.log
import core.model.Country
import datetime.DateTime
import org.json.JSONArray
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
    val jsonArray = JSONArray(json)
    for (i in 0 until jsonArray.length()) {
      val item = jsonArray.get(i) as JSONObject
      log { item.toString() }
      if (item.has("confirmed")
          && item.has("deaths")
          && item.has("recovered")
          && item.has("countrycode")
          && item.has("location")) {
        val country = Country(
          id = i,
          name = item.getString("countryregion"),
          iso2 = (item.get("countrycode") as JSONObject).getString("iso2"),
          confirmed = item.getString("confirmed").toInt(),
          deaths = item.getString("deaths").toInt(),
          recovered = item.getString("recovered").toInt(),
          latitude = (item.get("location") as JSONObject).getString("lat").toDouble(),
          longitude = (item.get("location") as JSONObject).getString("lng").toDouble()
        )
        countriesList.add(country)
      }
    }
    return TimedData(countriesList, DateTime.current())
  }
  
  override fun loadToCache(result: TimedData<List<Country>>) {
    threader.ioWorker.submit {
      saver.execute(synchronosly = true) {
        putString(COUNTRIES_INFO_LAST_UPDATE_TIME, DateTime.current().toString())
      }
    }
    threader.ioWorker.submit {
      sqLiteExecutor.saveCountriesInfo(result.data)
    }
  }
  
  companion object {
    const val SAVER_FILENAME = "CountriesInfoListenableExecutor"
    private const val COUNTRIES_INFO_LAST_UPDATE_TIME = "countriesInfoLastUpdate"
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true"
  }
}