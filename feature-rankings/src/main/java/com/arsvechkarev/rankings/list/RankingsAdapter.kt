package com.arsvechkarev.rankings.list

import api.threading.Threader
import base.views.SmallStatsView
import com.arsvechkarev.rankings.R
import com.arsvechkarev.recycler.ListAdapter
import com.arsvechkarev.recycler.delegate
import com.arsvechkarev.viewdsl.id
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.paddingsRes
import com.arsvechkarev.viewdsl.tag
import core.model.DisplayableCountry

class RankingsAdapter(
  onClick: (DisplayableCountry) -> Unit,
  threader: Threader
) : ListAdapter(threader) {
  
  init {
    addDelegates(
      delegate<DisplayableCountry> {
        view { parent ->
          SmallStatsView(parent.context).apply {
            id(R.id.small_stats_id)
            tag(SmallStatsView::class.java.name)
            paddingsRes(
              R.dimen.rankings_small_stats_view_p_start,
              R.dimen.rankings_small_stats_view_p_vertical,
              R.dimen.rankings_small_stats_view_p_end,
              R.dimen.rankings_small_stats_view_p_vertical
            )
          }
        }
        onInitViewHolder {
          itemView.onClick { onClick(item) }
        }
        onBind { itemView, country ->
          require(itemView is SmallStatsView)
          itemView.updateData(country.number, country.name, country.amountString)
        }
      }
    )
  }
}