package com.arsvechkarev.stats.list

import core.model.TYPE_COUNTRY_INFO
import core.recycler.BaseListAdapter

class StatsAdapter : BaseListAdapter() {
  
  init {
    delegates.put(TYPE_COUNTRY_INFO, CountryInfoAdapterDelegate())
  }
}