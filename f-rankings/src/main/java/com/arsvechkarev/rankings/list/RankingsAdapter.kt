package com.arsvechkarev.rankings.list

import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.StatsSmallView
import core.extenstions.i
import core.model.DisplayableCountry
import core.recycler.BaseListAdapter
import core.recycler.DifferentiableItem.AlwaysFalseCallback
import core.recycler.delegate

class RankingsAdapter : BaseListAdapter(
  delegate<DisplayableCountry> {
    
    view { parent ->
      val textSize = parent.context.resources.getDimension(
        R.dimen.rankings_small_stats_view_text_size)
      
      StatsSmallView(parent.context, textSize).apply {
        val pSmall = context.resources.getDimension(R.dimen.rankings_small_stats_view_p_end).i
        val pBig = context.resources.getDimension(R.dimen.rankings_small_stats_view_p_start).i
        val pVertical = context.resources.getDimension(
          R.dimen.rankings_small_stats_view_p_vertical).i
        setPadding(pBig, pVertical, pSmall, pVertical)
      }
    }
    
    onBind { itemView, country ->
      val statsSmallView = itemView as StatsSmallView
      statsSmallView.updateData(country.number, country.name, country.amountString)
    }
  },
  diffCallback = AlwaysFalseCallback
)