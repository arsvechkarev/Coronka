package com.arsvechkarev.rankings.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.SmallStatsView
import core.extenstions.i
import core.model.DisplayableCountry
import core.recycler.ListAdapterDelegate
import core.recycler.SortableDisplayableItem

class CountryItemDelegateAdapterDelegate : ListAdapterDelegate(DisplayableCountry::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val statsView = SmallStatsView(parent.context).apply {
      val pHorizontal = context.resources.getDimension(R.dimen.rankings_small_stats_view_p_horizontal).i
      val pVertical = context.resources.getDimension(R.dimen.rankings_small_stats_view_p_vertical).i
      setPadding(pHorizontal, pVertical, pHorizontal, pVertical)
    }
    return CountryItemViewHolder(statsView)
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SortableDisplayableItem) {
    (holder as CountryItemViewHolder).bind(item as DisplayableCountry)
  }
  
  class CountryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    fun bind(item: DisplayableCountry) {
      (itemView as SmallStatsView).updateData(item.number, item.name, item.amount)
    }
  }
}