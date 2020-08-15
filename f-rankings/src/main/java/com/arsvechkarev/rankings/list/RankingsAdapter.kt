package com.arsvechkarev.rankings.list

import core.recycler.BaseListAdapter

class RankingsAdapter : BaseListAdapter() {
  
  init {
    addDelegate(CountryItemDelegateAdapterDelegate())
  }
}