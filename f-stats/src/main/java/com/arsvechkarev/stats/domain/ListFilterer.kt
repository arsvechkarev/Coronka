package com.arsvechkarev.stats.domain

import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import com.arsvechkarev.storage.PopulationsTable
import com.arsvechkarev.storage.dao.PopulationsDao
import core.concurrency.Threader
import core.extenstions.assertThat
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createSuccessHandler
import core.model.Country
import core.model.DisplayableCountry
import core.model.DisplayableGeneralInfo
import core.model.GeneralInfo
import core.model.OptionType
import core.model.OptionType.PERCENT_BY_COUNTRY
import core.model.Population
import core.recycler.DisplayableItem
import core.releasable.Releasable

class ListFilterer(
  private val threader: Threader,
  private val populationsDao: PopulationsDao
) : Releasable {
  
  private var filterHandler: SuccessHandler<List<DisplayableItem>>? = null
  private val populations = ArrayList<Population>()
  
  fun filter(
    list: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType,
    action: SuccessAction<List<DisplayableItem>>
  ) {
    filterHandler = createSuccessHandler(action)
    threader.onBackground {
      if (optionType == PERCENT_BY_COUNTRY) {
        if (populations.isEmpty()) {
          val future = threader.onIoThread {
            DatabaseManager.instance.readableDatabase.use {
              val cursor = DatabaseExecutor.readAll(it, PopulationsTable.TABLE_NAME)
              val elements = populationsDao.getAll(cursor)
              populations.addAll(elements)
            }
          }
          future.get()
        }
        assertThat(populations.isNotEmpty())
        transformPopulations(populations, list, generalInfo, optionType)
      } else {
        transformOther(list, generalInfo, optionType)
      }
    }
  }
  
  private fun transformOther(
    countries: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ) {
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
    notifyDone(displayableCountries, generalInfo, optionType)
  }
  
  private fun transformPopulations(
    populations: List<Population>,
    countries: List<Country>,
    generalInfo: GeneralInfo,
    optionType: OptionType
  ) {
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
    notifyDone(displayableCountries, generalInfo, optionType)
  }
  
  private fun notifyDone(countries: List<DisplayableCountry>, generalInfo: GeneralInfo, optionType: OptionType) {
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
    threader.onMainThread {
      filterHandler?.dispatchSuccess(items)
    }
  }
  
  private fun determineNumber(type: OptionType, country: Country): Number = when (type) {
    OptionType.CONFIRMED -> country.confirmed
    OptionType.DEATHS -> country.deaths
    OptionType.RECOVERED -> country.recovered
    OptionType.DEATH_RATE -> country.deaths.toFloat() / country.confirmed.toFloat()
    else -> throw IllegalStateException("Unexpected type: $type")
  }
  
  override fun release() {
    filterHandler = null
  }
}