package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.rankings.list.HeaderItemAdapterDelegate
import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import core.db.CountriesMetaInfoDao
import core.db.CountriesMetaInfoTable
import core.extenstions.assertThat
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DisplayableCountry
import core.model.GeneralInfo
import core.model.OptionType
import core.model.OptionType.PERCENT_BY_COUNTRY
import core.model.WorldRegion
import core.recycler.SortableDisplayableItem

class ListFilterer(private val countriesMetaInfoDao: CountriesMetaInfoDao) {
  
  private val metaInfoList = ArrayList<CountryMetaInfo>()
  
  fun filter(
    list: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType,
    worldRegion: WorldRegion
  ): List<SortableDisplayableItem> {
    if (optionType == PERCENT_BY_COUNTRY) {
      if (metaInfoList.isEmpty()) {
        DatabaseManager.instance.readableDatabase.use {
          val cursor = DatabaseExecutor.readAll(it, CountriesMetaInfoTable.TABLE_NAME)
          val elements = countriesMetaInfoDao.getAll(cursor)
          metaInfoList.addAll(elements)
        }
      }
      assertThat(metaInfoList.isNotEmpty())
      return transformPopulations(metaInfoList, list, generalInfo, optionType, worldRegion)
    } else {
      return transformOther(list, optionType, worldRegion)
    }
  }
  
  private fun transformPopulations(
    metaInfoList: List<CountryMetaInfo>,
    countries: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType,
    worldRegion: WorldRegion
  ): List<SortableDisplayableItem> {
    val displayableCountries = ArrayList<DisplayableCountry>()
    for (i in countries.indices) {
      val country = countries[i]
      val population = metaInfoList.find { it.iso2 == country.iso2 }!!
      val number = country.confirmed.toFloat() / population.population.toFloat()
      displayableCountries.add(DisplayableCountry(country.name, number))
    }
    displayableCountries.sortDescending()
    for (i in countries.indices) {
      displayableCountries[i].number = i + 1
    }
    return notifyDone(displayableCountries)
  }
  
  private fun transformOther(
    countries: List<Country>,
    optionType: OptionType,
    worldRegion: WorldRegion
  ): List<SortableDisplayableItem> {
    val displayableCountries = ArrayList<DisplayableCountry>()
    for (i in countries.indices) {
      val it = countries[i]
      val number = determineNumber(optionType, it)
      displayableCountries.add(DisplayableCountry(it.name, number))
    }
    displayableCountries.sortDescending()
    for (i in countries.indices) {
      displayableCountries[i].number = i + 1
    }
    return notifyDone(displayableCountries)
  }
  
  private fun notifyDone(
    countries: List<DisplayableCountry>
  ): List<SortableDisplayableItem> {
    val items = ArrayList<SortableDisplayableItem>()
    items.add(HeaderItemAdapterDelegate.Header)
    items.addAll(countries)
    return items
  }
  
  private fun determineNumber(type: OptionType, country: Country): Number = when (type) {
    OptionType.CONFIRMED -> country.confirmed
    OptionType.DEATHS -> country.deaths
    OptionType.RECOVERED -> country.recovered
    OptionType.DEATH_RATE -> country.deaths.toFloat() / country.confirmed.toFloat()
    else -> throw IllegalStateException("Unexpected type: $type")
  }
}