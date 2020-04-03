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
import core.Application.Singletons.numberFormatter
import core.extenstions.inflate
import core.model.GeneralInfo
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_header.view.chipConfirmed
import kotlinx.android.synthetic.main.item_header.view.chipDeathRate
import kotlinx.android.synthetic.main.item_header.view.chipDeaths
import kotlinx.android.synthetic.main.item_header.view.chipPercentByCountry
import kotlinx.android.synthetic.main.item_header.view.chipRecovered
import kotlinx.android.synthetic.main.item_header.view.textConfirmed
import kotlinx.android.synthetic.main.item_header.view.textDeaths
import kotlinx.android.synthetic.main.item_header.view.textRecovered

class HeaderAdapterDelegate(private val onOptionClick: (OptionType) -> Unit) : AdapterDelegate {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return HeaderViewHolder(parent.inflate(R.layout.item_header))
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as HeaderViewHolder).bind(item as GeneralInfo)
  }
  
  inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private var currentChip = itemView.chipConfirmed
    
    init {
      currentChip.isActive = true
      itemView.chipConfirmed.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipRecovered.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeaths.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeathRate.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipPercentByCountry.setOnClickListener { notifyOnClick(it as Chip) }
    }
  
    fun bind(generalInfo: GeneralInfo) {
      itemView.textConfirmed.text = numberFormatter.format(generalInfo.confirmed)
      itemView.textRecovered.text = numberFormatter.format(generalInfo.recovered)
      itemView.textDeaths.text = numberFormatter.format(generalInfo.deaths)
    }
  
    fun updateChip(optionType: OptionType) {
      itemView.chipConfirmed.isActive = false
      itemView.chipRecovered.isActive = false
      itemView.chipDeaths.isActive = false
      itemView.chipDeathRate.isActive = false
      itemView.chipPercentByCountry.isActive = false
      val chip = getChipByType(optionType)
      chip.isActive = true
      currentChip = chip
    }
  
    private fun notifyOnClick(chip: Chip) {
      if (currentChip == chip) return
      currentChip.isActive = false
      chip.isActive = true
      currentChip = chip
      onOptionClick(getTypeByChip(currentChip))
    }
  
    private fun getChipByType(optionType: OptionType): Chip = when (optionType) {
      CONFIRMED -> itemView.chipConfirmed
      RECOVERED -> itemView.chipRecovered
      DEATHS -> itemView.chipDeaths
      DEATH_RATE -> itemView.chipDeathRate
      PERCENT_BY_COUNTRY -> itemView.chipPercentByCountry
    }
  
    private fun getTypeByChip(chip: Chip) = when (chip) {
      itemView.chipConfirmed -> CONFIRMED
      itemView.chipRecovered -> RECOVERED
      itemView.chipDeaths -> DEATHS
      itemView.chipDeathRate -> DEATH_RATE
      itemView.chipPercentByCountry -> PERCENT_BY_COUNTRY
      else -> throw IllegalStateException("Wat?")
    }
  }
}