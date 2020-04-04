package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import core.Loggable
import core.log
import core.model.Country
import org.json.JSONArray
import org.json.JSONObject

class CountriesInfoListenableExecutor(
  private val networker: Networker,
  private val sqLiteExecutor: CountriesSQLiteExecutor
) : BaseListenableExecutor<List<Country>>(), Loggable {
  
  override val logTag = "CountriesInfoListenableExecutor"
  
  override fun performCacheRequest(): List<Country>? {
    if (sqLiteExecutor.isTableNotEmpty()) {
      return sqLiteExecutor.readFromDatabase()
    }
    return null
  }
  
  override fun performNetworkRequest(): List<Country> {
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
          countryId = i,
          countryName = item.getString("countryregion"),
          countryCode = (item.get("countrycode") as JSONObject).getString("iso2"),
          confirmed = item.getString("confirmed").toInt(),
          deaths = item.getString("deaths").toInt(),
          recovered = item.getString("recovered").toInt(),
          latitude = (item.get("location") as JSONObject).getString("lat").toDouble(),
          longitude = (item.get("location") as JSONObject).getString("lng").toDouble()
        )
        countriesList.add(country)
      }
    }
    return countriesList
  }
  
  override fun loadToCache(result: List<Country>) {
    sqLiteExecutor.saveCountriesInfo(result)
  }
  
  companion object {
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true"
  }
}