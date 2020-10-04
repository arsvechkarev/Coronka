package com.arsvechkarev.rankings.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.StatsSmallView
import core.extenstions.i
import core.model.DisplayableCountry
import core.recycler.ListAdapterDelegate
import core.recycler.SortableDisplayableItem

class CountryItemAdapterDelegate : ListAdapterDelegate(DisplayableCountry::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val textSize = parent.context.resources.getDimension(
      R.dimen.rankings_small_stats_view_text_size)
    val statsView = StatsSmallView(parent.context, textSize).apply {
      val pSmall = context.resources.getDimension(
        R.dimen.rankings_small_stats_view_p_end).i
      val pBig = context.resources.getDimension(
        R.dimen.rankings_small_stats_view_p_start).i
      val pVertical = context.resources.getDimension(
        R.dimen.rankings_small_stats_view_p_vertical).i
      setPadding(pBig, pVertical, pSmall, pVertical)
    }
    return CountryItemViewHolder(statsView)
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SortableDisplayableItem) {
    (holder as CountryItemViewHolder).bind(item as DisplayableCountry)
  }
  
  class CountryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    fun bind(item: DisplayableCountry) {
      (itemView as StatsSmallView).updateData(item.number, item.name, item.amountString)
    }
  }
}