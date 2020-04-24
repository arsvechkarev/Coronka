package com.arsvechkarev.stats.list

import core.model.OptionType
import core.recycler.BaseAdapter

class StatsAdapter(
  onOptionClick: (OptionType) -> Unit,
  onOptionExplanationClick: (String) -> Unit
) : BaseAdapter() {
  
  init {
    addDelegate(HeaderAdapterDelegate(onOptionClick, onOptionExplanationClick))
    addDelegate(CountryInfoAdapterDelegate())
  }
}