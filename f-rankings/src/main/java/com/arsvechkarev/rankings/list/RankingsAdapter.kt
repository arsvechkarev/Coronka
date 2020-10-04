package com.arsvechkarev.rankings.list

import core.recycler.BaseListAdapter
import core.recycler.SortableDisplayableItem.AlwaysFalseCallback

class RankingsAdapter : BaseListAdapter(
  delegates = listOf(CountryItemAdapterDelegate()),
  diffCallback = AlwaysFalseCallback
)