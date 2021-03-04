package com.arsvechkarev.common

import core.Networker
import core.model.Country
import core.model.GeneralInfo
import core.model.TotalData
import io.reactivex.Observable
import org.json.JSONObject

class AllCountriesRepository(
  private val networker: Networker
) {
  
  fun getTotalData(): Observable<TotalData> {
    return networker.request(URL).map(::transformJson)
  }
  
  private fun transformJson(json: String): TotalData {
    val countriesList = ArrayList<Country>()
    val jsonObject = JSONObject(json)
    val jsonGlobalInfo = jsonObject.getJSONObject("Global")
    val generalInfo = GeneralInfo(
      jsonGlobalInfo.getString("TotalConfirmed").toInt(),
      jsonGlobalInfo.getString("TotalDeaths").toInt(),
      jsonGlobalInfo.getString("TotalRecovered").toInt()
    )
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
        recovered = item.getString("TotalRecovered").toInt(),
        newConfirmed = item.getString("NewConfirmed").toInt(),
        newDeaths = item.getString("NewDeaths").toInt()
      )
      countriesList.add(country)
    }
    return TotalData(countriesList, generalInfo)
  }
  
  companion object {
  
    const val URL = "https://api.covid19api.com/summary"
  }
}