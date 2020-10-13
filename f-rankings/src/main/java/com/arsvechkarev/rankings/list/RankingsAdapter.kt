package com.arsvechkarev.rankings.list

import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.StatsSmallView
import core.extenstions.assertThat
import core.model.DisplayableCountry
import core.recycler.BaseListAdapter
import core.recycler.DifferentiableItem.AlwaysFalseCallback
import core.recycler.delegate

class RankingsAdapter : BaseListAdapter(
  delegate<DisplayableCountry> {
    
    buildView {
      StatsSmallView(context).apply {
        paddingsRes(
          R.dimen.rankings_small_stats_view_p_start,
          R.dimen.rankings_small_stats_view_p_vertical,
          R.dimen.rankings_small_stats_view_p_end,
          R.dimen.rankings_small_stats_view_p_vertical
        )
      }
    }
    
    onBind { itemView, country ->
      assertThat(itemView is StatsSmallView)
      itemView.updateData(country.number, country.name, country.amountString)
    }
  },
  diffCallback = AlwaysFalseCallback
)