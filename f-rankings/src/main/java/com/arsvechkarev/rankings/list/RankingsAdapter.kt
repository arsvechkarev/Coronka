package com.arsvechkarev.rankings.list

import core.recycler.BaseListAdapter

class RankingsAdapter : BaseListAdapter() {
  
  init {
    addDelegate(HeaderItemAdapterDelegate())
    addDelegate(CountryItemAdapterDelegate())
  }
}