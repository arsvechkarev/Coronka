package com.arsvechkarev.stats.list

import core.model.OptionType
import core.recycler.BaseAdapter
import core.recycler.SortableDisplayableItem

class StatsAdapter(
  onOptionClick: (OptionType) -> Unit,
  onOptionExplanationClick: (String) -> Unit
) : BaseAdapter() {
  
  init {
    addDelegate(HeaderAdapterDelegate(onOptionClick, onOptionExplanationClick))
    //    addDelegate(CountryInfoAdapterDelegate())
  }
  
  fun updateFiltered(list: List<SortableDisplayableItem>) {
    //    submitList(list, notify = false)
    //    notifyItemRangeChanged(1, data.size)
  }
}