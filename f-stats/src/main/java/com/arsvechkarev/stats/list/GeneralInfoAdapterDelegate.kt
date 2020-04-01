package com.arsvechkarev.stats.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.stats.R
import core.extenstions.inflate
import core.model.GeneralInfo
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_general_info.view.textConfirmed
import kotlinx.android.synthetic.main.item_general_info.view.textDeaths
import kotlinx.android.synthetic.main.item_general_info.view.textRecovered

class GeneralInfoAdapterDelegate : AdapterDelegate {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return GeneralInfoViewHolder(parent.inflate(R.layout.item_general_info))
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as GeneralInfoViewHolder).bind(item as GeneralInfo)
  }
  
  inner class GeneralInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    fun bind(generalInfo: GeneralInfo) {
      itemView.textConfirmed.text = generalInfo.confirmed.toString()
      itemView.textRecovered.text = generalInfo.recovered.toString()
      itemView.textDeaths.text = generalInfo.deaths.toString()
    }
  }
}