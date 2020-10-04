package com.arsvechkarev.rankings.presentation

import core.Application
import core.Application.decimalFormatter
import core.extenstions.assertThat
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.WorldRegion
import core.recycler.SortableDisplayableItem

class CountriesFilterer(
  private val countries: List<Country>,
  private val metaInfoList: List<CountryMetaInfo>
) {
  
  private val regionsToCountries = HashMap<String, MutableList<Country>>()
  private val cache = HashMap<Pair<OptionType, WorldRegion>, List<SortableDisplayableItem>>()
  
  init {
    metaInfoList.forEach { metaInfo ->
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
  ): List<SortableDisplayableItem> {
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
      (displayableCountries[i] as DisplayableCountry).number = (i + 1)
    }
    cache[Pair(optionType, worldRegion)] = displayableCountries
    return displayableCountries
  }
  
  private fun filterCountries(
    optionType: OptionType,
    worldRegion: WorldRegion
  ): MutableList<SortableDisplayableItem> {
    if (optionType == OptionType.PERCENT_BY_COUNTRY) {
      return performPercentByCountryFiltering(worldRegion)
    }
    val items = ArrayList<SortableDisplayableItem>()
    val list = if (worldRegion == WorldRegion.WORLDWIDE) {
      countries
    } else {
      regionsToCountries.getValue(worldRegion.letters!!)
    }
    for (country in list) {
      val amount = determineAmount(optionType, country)
      val amountString = when (amount) {
        is Float -> if (amount == 0f) "0%" else "${decimalFormatter.format(amount)}%"
        else -> Application.numberFormatter.format(amount)
      }
      items.add(DisplayableCountry(country.name, amount, amountString))
    }
    return items
  }
  
  private fun performPercentByCountryFiltering(
    worldRegion: WorldRegion
  ): MutableList<SortableDisplayableItem> {
    val items = ArrayList<SortableDisplayableItem>()
    val list = if (worldRegion == WorldRegion.WORLDWIDE) {
      countries
    } else {
      regionsToCountries.getValue(worldRegion.letters!!)
    }
    for (country in list) {
      if (country.isFromRegion(worldRegion)) {
        val countryMetaInfo = metaInfoList.find { it.iso2 == country.iso2 }!!
        val amount = country.confirmed.toFloat() / countryMetaInfo.population.toFloat() * 100
        val amountString = if (amount == 0f) "0%" else "${
          decimalFormatter.format(amount)
        }%"
        items.add(DisplayableCountry(country.name, amount, amountString))
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
    return metaInfoList.find { it.iso2 == this.iso2 }!!.worldRegion == worldRegion.letters
  }
}
