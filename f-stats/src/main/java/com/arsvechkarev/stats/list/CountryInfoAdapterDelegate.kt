package com.arsvechkarev.stats.list

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.stats.R
import com.arsvechkarev.views.SmallStatsView
import core.model.DisplayableCountry
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem

class CountryInfoAdapterDelegate : AdapterDelegate(DisplayableCountry::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val textSize = parent.resources.getDimensionPixelSize(R.dimen.text_h4)
    val paddingHorizontal = parent.resources.getDimensionPixelSize(R.dimen.stats_view_p_horizontal)
    val paddingVertical = parent.resources.getDimensionPixelSize(R.dimen.stats_view_p_vertical)
    val statsView = SmallStatsView(parent.context, textSize.toFloat())
    statsView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    statsView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
    return CountryViewHolder(statsView)
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as CountryViewHolder).bind(item as DisplayableCountry)
  }
  
  inner class CountryViewHolder(itemView: SmallStatsView) : RecyclerView.ViewHolder(itemView) {
    
    fun bind(country: DisplayableCountry) {
      val statsView = itemView as SmallStatsView
      statsView.updateData(country.number, country.name, country.amount)
    }
  }
}