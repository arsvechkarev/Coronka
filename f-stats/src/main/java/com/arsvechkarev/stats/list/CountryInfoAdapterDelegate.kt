package com.arsvechkarev.stats.list

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.views.SmallStatsView
import core.extenstions.dpInt
import core.model.DisplayableCountry
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem

class CountryInfoAdapterDelegate : AdapterDelegate {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val statsView = SmallStatsView(parent.context)
    statsView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    statsView.setPadding(16.dpInt, 8.dpInt, 8.dpInt, 16.dpInt)
    return CountryViewHolder(statsView)
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as CountryViewHolder).bind(item as DisplayableCountry)
  }
  
  inner class CountryViewHolder(itemView: SmallStatsView) : RecyclerView.ViewHolder(itemView) {
    
    fun bind(country: DisplayableCountry) {
      val statsView = itemView as SmallStatsView
      statsView.updateData(country.name, country.number)
    }
  }
}