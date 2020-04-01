package com.arsvechkarev.stats.list

import core.model.TYPE_COUNTRY_INFO
import core.model.TYPE_GENERAL_INFO
import core.model.TYPE_OPTIONS
import core.recycler.BaseListAdapter

class StatsAdapter(onOptionClick: (OptionType) -> Unit) : BaseListAdapter() {
  
  init {
    delegates.put(TYPE_GENERAL_INFO, GeneralInfoAdapterDelegate())
    delegates.put(TYPE_OPTIONS, OptionsAdapterDelegate(onOptionClick))
    delegates.put(TYPE_COUNTRY_INFO, CountryInfoAdapterDelegate())
  }
}