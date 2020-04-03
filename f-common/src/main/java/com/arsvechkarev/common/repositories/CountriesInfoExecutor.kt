package com.arsvechkarev.common.repositories

import com.arsvechkarev.network.Networker
import core.Application
import core.Loggable
import core.handlers.ResultHandler
import core.log
import core.model.Country
import core.releasable.Releasable
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class CountriesInfoExecutor(
  private val threader: Application.Threader,
  private val networker: Networker
) : Releasable, Loggable {
  
  override val logTag = "Network_CountriesInfo"
  
  private val isLoadingNow = AtomicBoolean(false)
  private val listeners = CopyOnWriteArrayList<ResultHandler<List<Country>, Throwable>>()
  
  fun getCountriesInfoAsync(resultHandler: ResultHandler<List<Country>, Throwable>) {
    listeners.add(resultHandler)
    if (isLoadingNow.get()) {
      return
    }
    isLoadingNow.set(true)
    try {
      threader.ioWorker.submit {
        val json = networker.performRequest(URL)
        val countriesList = transformJson(json)
        threader.mainThreadWorker.submit {
          listeners.forEach { it.dispatchSuccess(countriesList) }
          isLoadingNow.set(false)
        }
      }
    } catch (e: Throwable) {
      log(e)
      threader.mainThreadWorker.submit {
        listeners.forEach { it.dispatchFailure(e) }
        isLoadingNow.set(false)
      }
    }
  }
  
  fun removeListener(listener: ResultHandler<List<Country>, Throwable>) {
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
  
  override fun release() {
    listeners.clear()
  }
  
  companion object {
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true"
  }
}