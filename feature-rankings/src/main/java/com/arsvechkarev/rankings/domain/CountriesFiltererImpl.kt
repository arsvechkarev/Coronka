package com.arsvechkarev.rankings.domain

import api.recycler.DifferentiableItem
import base.extensions.toFormattedDecimalNumber
import base.extensions.toFormattedNumber
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.WorldRegion

class CountriesFiltererImpl : CountriesFilterer {
  
  private val regionsToCountries = HashMap<String, MutableList<Country>>()
  private val cache = HashMap<Pair<OptionType, WorldRegion>, List<DifferentiableItem>>()
  private var countriesMetaInfo: Map<String, CountryMetaInfo> = HashMap()
  private var countries: List<Country> = ArrayList()
  
  override fun filterInitial(
    countries: List<Country>,
    countriesMetaInfo: Map<String, CountryMetaInfo>,
    worldRegion: WorldRegion,
    optionType: OptionType
  ): List<DifferentiableItem> {
    initValues(countries, countriesMetaInfo)
    return filter(worldRegion, optionType)
  }
  
  override fun filter(worldRegion: WorldRegion, optionType: OptionType): List<DifferentiableItem> {
    require(countries.isNotEmpty()) { "Countries list is empty" }
    require(countriesMetaInfo.isNotEmpty()) { "Countries meta info map list is empty" }
    cache[Pair(optionType, worldRegion)]?.let { list ->
      return list
    }
    val displayableCountries = filterCountries(worldRegion, optionType)
    displayableCountries.sortWith(Comparator { item1, item2 ->
      require(item1 is DisplayableCountry && item2 is DisplayableCountry)
      return@Comparator item2.compareTo(item1)
    })
    for (i in 0 until displayableCountries.size) {
      (displayableCountries[i] as DisplayableCountry).number = i + 1
    }
    cache[Pair(optionType, worldRegion)] = displayableCountries
    return displayableCountries
  }
  
  private fun filterCountries(
    worldRegion: WorldRegion,
    optionType: OptionType
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
        val countryMetaInfo = countriesMetaInfo.getValue(country.iso2)
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
    return countriesMetaInfo.getValue(this.iso2).worldRegion == worldRegion.letters
  }
  
  private fun initValues(
    countries: List<Country>,
    countriesMetaInfo: Map<String, CountryMetaInfo>
  ) {
    this.countries = countries
    this.countriesMetaInfo = countriesMetaInfo
    countriesMetaInfo.values.forEach { metaInfo ->
      val list = regionsToCountries[metaInfo.worldRegion] ?: mutableListOf()
      val country = countries.find { it.iso2 == metaInfo.iso2 }
      if (country != null) {
        list.add(country)
        regionsToCountries[metaInfo.worldRegion] = list
      }
    }
  }
}