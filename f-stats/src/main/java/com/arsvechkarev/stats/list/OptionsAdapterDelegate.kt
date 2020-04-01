package com.arsvechkarev.stats.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.list.OptionType.CONFIRMED
import com.arsvechkarev.stats.list.OptionType.DEATHS
import com.arsvechkarev.stats.list.OptionType.DEATH_RATE
import com.arsvechkarev.stats.list.OptionType.PERCENT_BY_COUNTRY
import com.arsvechkarev.stats.list.OptionType.RECOVERED
import com.arsvechkarev.views.Chip
import core.extenstions.inflate
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_options.view.chipConfirmed
import kotlinx.android.synthetic.main.item_options.view.chipDeathRate
import kotlinx.android.synthetic.main.item_options.view.chipDeaths
import kotlinx.android.synthetic.main.item_options.view.chipPercentByCountry
import kotlinx.android.synthetic.main.item_options.view.chipRecovered

class OptionsAdapterDelegate(private val onOptionClick: (OptionType) -> Unit) : AdapterDelegate {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return OptionsViewHolder(parent.inflate(R.layout.item_options))
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {}
  
  inner class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private var currentChip = itemView.chipConfirmed
    
    init {
      currentChip.isActive = true
      itemView.chipConfirmed.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipRecovered.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeaths.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeathRate.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipPercentByCountry.setOnClickListener { notifyOnClick(it as Chip) }
    }
    
    private fun notifyOnClick(chip: Chip) {
      if (currentChip == chip) return
      currentChip.isActive = false
      chip.isActive = true
      currentChip = chip
      onOptionClick(getTypeByChip(currentChip))
    }
    
    private fun getTypeByChip(chip: Chip) = when (chip) {
      itemView.chipConfirmed -> CONFIRMED
      itemView.chipDeaths -> DEATHS
      itemView.chipRecovered -> RECOVERED
      itemView.chipDeathRate -> DEATH_RATE
      itemView.chipPercentByCountry -> PERCENT_BY_COUNTRY
      else -> throw IllegalStateException("Wat?")
    }
  }
}