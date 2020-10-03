package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.rankings.list.HeaderItemAdapterDelegate.Header
import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import core.db.CountriesMetaInfoDao
import core.db.CountriesMetaInfoTable
import core.extenstions.assertThat
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.OptionType
import core.model.WorldRegion
import core.recycler.SortableDisplayableItem

class ListFilterer(private val countriesMetaInfoDao: CountriesMetaInfoDao) {
  
  private val metaInfoList = ArrayList<CountryMetaInfo>()
  
  fun filter(
    list: List<Country>,
    optionType: OptionType,
    worldRegion: WorldRegion,
  ): List<SortableDisplayableItem> {
    if (metaInfoList.isEmpty()) {
      DatabaseManager.instance.readableDatabase.use {
        val cursor = DatabaseExecutor.readAll(it, CountriesMetaInfoTable.TABLE_NAME)
        val elements = countriesMetaInfoDao.getAll(cursor)
        metaInfoList.addAll(elements)
      }
    }
    assertThat(metaInfoList.isNotEmpty())
    return performFiltering(list, optionType, worldRegion)
  }
  
  private fun performFiltering(
    countries: List<Country>,
    optionType: OptionType,
    worldRegion: WorldRegion
  ): List<SortableDisplayableItem> {
    val displayableCountries = filterCountries(countries, optionType, worldRegion)
    displayableCountries.add(Header)
    displayableCountries.sortWith(Comparator { item1, item2 ->
      if (item1 is Header) return@Comparator -1
      if (item2 is Header) return@Comparator 1
      assertThat(item1 is DisplayableCountry && item2 is DisplayableCountry)
      return@Comparator item2.compareTo(item1)
    })
    for (i in 1 until displayableCountries.size) {
      (displayableCountries[i] as DisplayableCountry).number = i
    }
    return displayableCountries
  }
  
  private fun filterCountries(
    countries: List<Country>,
    optionType: OptionType,
    worldRegion: WorldRegion
  ): MutableList<SortableDisplayableItem> {
    if (optionType == OptionType.PERCENT_BY_COUNTRY) {
      return performPercentByCountryFiltering(countries, worldRegion)
    }
    val items = ArrayList<SortableDisplayableItem>()
    for (i in countries.indices) {
      val country = countries[i]
      if (country.isFromRegion(worldRegion)) {
        val number = determineNumber(optionType, country)
        items.add(DisplayableCountry(country.name, number))
      }
    }
    return items
  }
  
  private fun performPercentByCountryFiltering(
    countries: List<Country>,
    worldRegion: WorldRegion
  ): MutableList<SortableDisplayableItem> {
    val items = ArrayList<SortableDisplayableItem>()
    for (country in countries) {
      if (country.isFromRegion(worldRegion)) {
        val population = metaInfoList.find { it.iso2 == country.iso2 }!!
        val number = country.confirmed.toFloat() / population.population.toFloat()
        items.add(DisplayableCountry(country.name, number))
      }
    }
    return items
  }
  
  private fun determineNumber(type: OptionType, country: Country): Number = when (type) {
    OptionType.CONFIRMED -> country.confirmed
    OptionType.DEATHS -> country.deaths
    OptionType.RECOVERED -> country.recovered
    OptionType.DEATH_RATE -> country.deaths.toFloat() / country.confirmed.toFloat()
    else -> throw IllegalStateException("Unexpected type: $type")
  }
  
  private fun Country.isFromRegion(worldRegion: WorldRegion): Boolean {
    if (worldRegion == WorldRegion.WORLDWIDE) return true
    return metaInfoList.find { it.iso2 == this.iso2 }!!.worldRegion == worldRegion.letters
  }
}
