package com.arsvechkarev.stats.list

import core.model.DisplayableCountry
import core.model.TYPE_COUNTRY_INFO
import core.model.TYPE_HEADER
import core.recycler.BaseAdapter
import core.recycler.DisplayableItem

class StatsAdapter(onOptionClick: (OptionType) -> Unit) : BaseAdapter<DisplayableItem>() {
  
  init {
    delegates.put(TYPE_HEADER, HeaderAdapterDelegate(onOptionClick))
    delegates.put(TYPE_COUNTRY_INFO, CountryInfoAdapterDelegate())
  }
  
  fun updateFilteredCountries(countries: List<DisplayableCountry>) {
    data.subList(1, data.size).clear()
    data.addAll(1, countries)
    notifyItemRangeChanged(1, countries.size)
  }
}