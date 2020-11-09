package com.arsvechkarev.rankings.list

import com.arsvechkarev.rankings.R
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.paddingsRes
import com.arsvechkarev.views.SmallStatsView
import core.extenstions.assertThat
import core.model.DisplayableCountry
import core.recycler.CallbackType
import core.recycler.ListAdapter
import core.recycler.delegate

class RankingsAdapter(onClick: (DisplayableCountry) -> Unit) : ListAdapter(
  delegate<DisplayableCountry> {
    buildView {
      SmallStatsView(context).apply {
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
      assertThat(itemView is SmallStatsView)
      itemView.updateData(country.number, country.name, country.amountString)
    }
  },
  callbackType = CallbackType.ALWAYS_FALSE
)