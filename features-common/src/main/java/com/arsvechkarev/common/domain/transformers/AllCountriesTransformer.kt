package com.arsvechkarev.common.domain.transformers

import core.model.Country
import core.model.GeneralInfo
import core.model.TotalInfo
import org.json.JSONObject

object AllCountriesTransformer {
  
  private const val GLOBAL = "Global"
  private const val TOTAL_CONFIRMED = "TotalConfirmed"
  private const val TOTAL_DEATHS = "TotalDeaths"
  private const val TOTAL_RECOVERED = "TotalRecovered"
  private const val COUNTRIES = "Countries"
  private const val COUNTRY = "Country"
  private const val SLUG = "Slug"
  private const val COUNTRY_CODE = "CountryCode"
  private const val NEW_CONFIRMED = "NewConfirmed"
  private const val NEW_DEATHS = "NewDeaths"
  
  fun toTotalData(json: String): TotalInfo {
    val countriesList = ArrayList<Country>()
    val jsonObject = JSONObject(json)
    val jsonGlobalInfo = jsonObject.getJSONObject(GLOBAL)
    val generalInfo = GeneralInfo(
      jsonGlobalInfo.getString(TOTAL_CONFIRMED).toInt(),
      jsonGlobalInfo.getString(TOTAL_DEATHS).toInt(),
      jsonGlobalInfo.getString(TOTAL_RECOVERED).toInt()
    )
    val jsonArray = jsonObject.getJSONArray(COUNTRIES)
    for (i in 0 until jsonArray.length()) {
      val item = jsonArray.get(i) as JSONObject
      val country = Country(
        id = i,
        name = item.getString(COUNTRY),
        slug = item.getString(SLUG),
        iso2 = item.getString(COUNTRY_CODE),
        confirmed = item.getString(TOTAL_CONFIRMED).toInt(),
        deaths = item.getString(TOTAL_DEATHS).toInt(),
        recovered = item.getString(TOTAL_RECOVERED).toInt(),
        newConfirmed = item.getString(NEW_CONFIRMED).toInt(),
        newDeaths = item.getString(NEW_DEATHS).toInt()
      )
      countriesList.add(country)
    }
    return TotalInfo(countriesList, generalInfo)
  }
}