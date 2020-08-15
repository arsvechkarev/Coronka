package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import com.arsvechkarev.storage.PopulationsTable
import com.arsvechkarev.storage.dao.PopulationsDao
import core.extenstions.assertThat
import core.model.Country
import core.model.DisplayableCountry
import core.model.DisplayableGeneralInfo
import core.model.GeneralInfo
import core.model.OptionType
import core.model.OptionType.PERCENT_BY_COUNTRY
import core.model.Population
import core.recycler.DisplayableItem

class ListFilterer(private val populationsDao: PopulationsDao) {
  
  private val populations = ArrayList<Population>()
  
  fun filter(
    list: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ): List<DisplayableItem> {
    if (optionType == PERCENT_BY_COUNTRY) {
      if (populations.isEmpty()) {
        DatabaseManager.instance.readableDatabase.use {
          val cursor = DatabaseExecutor.readAll(it, PopulationsTable.TABLE_NAME)
          val elements = populationsDao.getAll(cursor)
          populations.addAll(elements)
        }
      }
      assertThat(populations.isNotEmpty())
      return transformPopulations(populations, list, generalInfo, optionType)
    } else {
      return transformOther(list, generalInfo, optionType)
    }
  }
  
  private fun transformPopulations(
    populations: List<Population>,
    countries: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ): List<DisplayableItem> {
    val displayableCountries = ArrayList<DisplayableCountry>()
    for (i in countries.indices) {
      val country = countries[i]
      val population = populations.find { it.iso2 == country.iso2 }!!
      val number = country.confirmed.toFloat() / population.population.toFloat()
      displayableCountries.add(DisplayableCountry(country.name, number))
    }
    displayableCountries.sortDescending()
    for (i in countries.indices) {
      displayableCountries[i].number = i + 1
    }
    return notifyDone(displayableCountries, generalInfo, optionType)
  }
  
  private fun transformOther(
    countries: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ): List<DisplayableItem> {
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
    return notifyDone(displayableCountries, generalInfo, optionType)
  }
  
  private fun notifyDone(
    countries: List<DisplayableCountry>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ): List<DisplayableItem> {
    val items = ArrayList<DisplayableItem>()
    items.add(
      DisplayableGeneralInfo(
        generalInfo.confirmed,
        generalInfo.deaths,
        generalInfo.recovered,
        optionType
      )
    )
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