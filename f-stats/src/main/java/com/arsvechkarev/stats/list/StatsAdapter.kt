package com.arsvechkarev.stats.list

import core.model.OptionType
import core.recycler.BaseAdapter
import core.recycler.DisplayableItem

class StatsAdapter(
  onOptionClick: (OptionType) -> Unit,
  onOptionExplanationClick: (String) -> Unit
) : BaseAdapter() {
  
  init {
    addDelegate(HeaderAdapterDelegate(onOptionClick, onOptionExplanationClick))
    addDelegate(CountryInfoAdapterDelegate())
  }
  
  fun updateFiltered(list: List<DisplayableItem>) {
    submitList(list, notify = false)
    notifyItemRangeChanged(1, data.size)
  }
}