package com.arsvechkarev.stats.list

import core.model.OptionType
import core.model.TYPE_COUNTRY_INFO
import core.model.TYPE_GENERAL_INFO
import core.recycler.BaseAdapter
import core.recycler.DisplayableItem

class StatsAdapter(onOptionClick: (OptionType) -> Unit) : BaseAdapter<DisplayableItem>() {
  
  init {
    delegates.put(TYPE_GENERAL_INFO, HeaderAdapterDelegate(onOptionClick))
    delegates.put(TYPE_COUNTRY_INFO, CountryInfoAdapterDelegate())
  }
}