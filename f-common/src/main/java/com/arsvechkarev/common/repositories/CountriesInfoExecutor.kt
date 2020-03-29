package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import core.ApplicationConfig
import core.Loggable
import core.log
import core.model.Country
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class CountriesInfoExecutor(
  private val threader: ApplicationConfig.Threader,
  private val networker: Networker
) : Loggable {
  
  override val logTag = "Network_CountriesInfo"
  
  private val isLoadingNow = AtomicBoolean(false)
  
  private val listeners = CopyOnWriteArrayList<CountriesInfoListener>()
  
  fun getCountriesInfoAsync(listener: CountriesInfoListener) {
    listeners.add(listener)
    if (isLoadingNow.get()) {
      return
    }
    isLoadingNow.set(true)
    try {
      threader.ioWorker.submit {
        val json = networker.performRequest(URL)
        val countriesList = transformJson(json)
        threader.mainThreadWorker.submit {
          listeners.forEach { it.onSuccess(countriesList) }
          isLoadingNow.set(false)
        }
      }
    } catch (e: Throwable) {
      log(e)
      threader.mainThreadWorker.submit {
        listeners.forEach { it.onFailure(e) }
        isLoadingNow.set(false)
      }
    }
  }
  
  fun removeListener(listener: CountriesInfoListener) {
    listeners.remove(listener)
  }
  
  private fun transformJson(json: String): List<Country> {
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
          confirmed = item.getString("confirmed"),
          deaths = item.getString("deaths"),
          recovered = item.getString("recovered"),
          latitude = (item.get("location") as JSONObject).getString("lat"),
          longitude = (item.get("location") as JSONObject).getString("lng")
        )
        countriesList.add(country)
      }
    }
    return countriesList
  }
  
  companion object {
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true"
  }
  
  interface CountriesInfoListener {
    
    fun onSuccess(countriesData: List<Country>)
    
    fun onFailure(throwable: Throwable)
  }
}