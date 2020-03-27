package com.arsvechkarev.map.repository

import com.arsvechkarev.network.Networker
import core.ApplicationConfig
import core.log.Loggable
import core.log.log
import core.model.Country
import core.model.print
import org.json.JSONArray
import org.json.JSONObject

class CountriesInfoExecutor(
  private val threader: ApplicationConfig.Threader,
  private val networker: Networker
) : Loggable {
  
  override val tag = "Network_CountriesInfo"
  
  fun getCountriesInfoAsync(onSuccess: (List<Country>) -> Unit, onFailure: (Throwable) -> Unit) {
    try {
      threader.ioWorker.submit {
        val result = networker.performRequest(URL)
        val countriesList = ArrayList<Country>()
        val jsonArray = JSONArray(result)
        for (i in 0 until jsonArray.length()) {
          val json = jsonArray.get(i) as JSONObject
          log { json.toString() }
          if (json.has("confirmed")
              && json.has("deaths")
              && json.has("recovered")
              && json.has("countrycode")
              && json.has("location")) {
            val country = Country(
              countryId = i,
              countryName = json.getString("countryregion"),
              countryCode = (json.get("countrycode") as JSONObject).getString("iso2"),
              confirmed = json.getString("confirmed"),
              deaths = json.getString("deaths"),
              recovered = json.getString("recovered"),
              latitude = (json.get("location") as JSONObject).getString("lat"),
              longitude = (json.get("location") as JSONObject).getString("lng")
            )
            countriesList.add(country)
          }
        }
        threader.mainThreadWorker.submit {
          countriesList.print("element")
          onSuccess(countriesList)
        }
      }
    } catch (e: Throwable) {
      log(e)
      onFailure(e)
    }
  }
  
  companion object {
    private const val URL = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest?onlyCountries=true"
  }
  
}