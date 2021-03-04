package com.arsvechkarev.rankings.presentation

import core.extenstions.assertThat
import core.extenstions.toFormattedDecimalNumber
import core.extenstions.toFormattedNumber
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.WorldRegion
import core.recycler.DifferentiableItem

class CountriesFilterer(
  private val countries: List<Country>,
  private val metaInfoList: Map<String, CountryMetaInfo>
) {
  
  private val regionsToCountries = HashMap<String, MutableList<Country>>()
  private val cache = HashMap<Pair<OptionType, WorldRegion>, List<DifferentiableItem>>()
  
  init {
    metaInfoList.values.forEach { metaInfo ->
      val list = regionsToCountries[metaInfo.worldRegion] ?: mutableListOf()
      val country = countries.find { it.iso2 == metaInfo.iso2 }
      if (country != null) {
        list.add(country)
        regionsToCountries[metaInfo.worldRegion] = list
      }
    }
  }
  
  fun filter(
    optionType: OptionType,
    worldRegion: WorldRegion,
  ): List<DifferentiableItem> {
    val cachedList = cache[Pair(optionType, worldRegion)]
    if (cachedList != null) {
      return cachedList
    }
    val displayableCountries = filterCountries(optionType, worldRegion)
    displayableCountries.sortWith(Comparator { item1, item2 ->
      assertThat(item1 is DisplayableCountry && item2 is DisplayableCountry)
      return@Comparator item2.compareTo(item1)
    })
    for (i in 0 until displayableCountries.size) {
      (displayableCountries[i] as DisplayableCountry).number = i + 1
    }
    cache[Pair(optionType, worldRegion)] = displayableCountries
    return displayableCountries
  }
  
  private fun filterCountries(
    optionType: OptionType,
    worldRegion: WorldRegion
  ): MutableList<DifferentiableItem> {
    if (optionType == OptionType.PERCENT_BY_COUNTRY) {
      return performPercentByCountryFiltering(worldRegion)
    }
    val items = ArrayList<DifferentiableItem>()
    val list = if (worldRegion == WorldRegion.WORLDWIDE) {
      countries
    } else {
      regionsToCountries.getValue(worldRegion.letters!!)
    }
    for (country in list) {
      when (val amount = determineAmount(optionType, country)) {
        is Float -> {
          if (amount > 0.001f) {
            val amountString = amount.toFormattedDecimalNumber()
            items.add(DisplayableCountry(country.name, amount, amountString, country))
          }
        }
        else -> {
          val amountString = amount.toFormattedNumber()
          items.add(DisplayableCountry(country.name, amount, amountString, country))
        }
      }
    }
    return items
  }
  
  private fun performPercentByCountryFiltering(
    worldRegion: WorldRegion
  ): MutableList<DifferentiableItem> {
    val items = ArrayList<DifferentiableItem>()
    val list = if (worldRegion == WorldRegion.WORLDWIDE) {
      countries
    } else {
      regionsToCountries.getValue(worldRegion.letters!!)
    }
    for (country in list) {
      if (country.isFromRegion(worldRegion)) {
        val countryMetaInfo = metaInfoList.getValue(country.iso2)
        val amount = country.confirmed.toFloat() / countryMetaInfo.population.toFloat() * 100
        if (amount >= 0.001f) {
          val amountString = amount.toFormattedDecimalNumber()
          items.add(DisplayableCountry(country.name, amount, amountString, country))
        }
      }
    }
    return items
  }
  
  private fun determineAmount(type: OptionType, country: Country): Number = when (type) {
    OptionType.CONFIRMED -> country.confirmed
    OptionType.DEATHS -> country.deaths
    OptionType.RECOVERED -> country.recovered
    OptionType.DEATH_RATE -> {
      if (country.confirmed == 0) {
        0f
      } else {
        country.deaths.toFloat() / country.confirmed.toFloat() * 100
      }
    }
    else -> throw IllegalStateException("Unexpected type: $type")
  }
  
  private fun Country.isFromRegion(worldRegion: WorldRegion): Boolean {
    if (worldRegion == WorldRegion.WORLDWIDE) return true
    return metaInfoList.getValue(this.iso2).worldRegion == worldRegion.letters
  }
}
