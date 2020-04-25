package com.arsvechkarev.stats.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.stats.R
import com.arsvechkarev.views.Chip
import core.Application.Singletons.numberFormatter
import core.extenstions.inflate
import core.model.DisplayableGeneralInfo
import core.model.OptionType
import core.model.OptionType.CONFIRMED
import core.model.OptionType.DEATHS
import core.model.OptionType.DEATH_RATE
import core.model.OptionType.PERCENT_BY_COUNTRY
import core.model.OptionType.RECOVERED
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_stats_header.view.chipConfirmed
import kotlinx.android.synthetic.main.item_stats_header.view.chipDeathRate
import kotlinx.android.synthetic.main.item_stats_header.view.chipDeaths
import kotlinx.android.synthetic.main.item_stats_header.view.chipPercentByCountry
import kotlinx.android.synthetic.main.item_stats_header.view.chipRecovered
import kotlinx.android.synthetic.main.item_stats_header.view.textConfirmed
import kotlinx.android.synthetic.main.item_stats_header.view.textDeaths
import kotlinx.android.synthetic.main.item_stats_header.view.textRecovered

class HeaderAdapterDelegate(
  private val onOptionClick: (OptionType) -> Unit,
  private val onExplanationIconClick: (String) -> Unit
) : AdapterDelegate(DisplayableGeneralInfo::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return HeaderViewHolder(parent.inflate(R.layout.item_stats_header))
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as HeaderViewHolder).bind(item as DisplayableGeneralInfo)
  }
  
  inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private var currentChip = itemView.chipConfirmed
    
    init {
      itemView.chipConfirmed.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipRecovered.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeaths.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeathRate.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipPercentByCountry.setOnClickListener { notifyOnClick(it as Chip) }
      itemView.chipDeathRate.onIconClicked {
        onExplanationIconClick(itemView.context.getString(R.string.text_death_rate_explanation))
      }
    }
    
    fun bind(generalInfo: DisplayableGeneralInfo) {
      updateChip(generalInfo.optionType)
      itemView.textConfirmed.text = numberFormatter.format(generalInfo.confirmed)
      itemView.textRecovered.text = numberFormatter.format(generalInfo.recovered)
      itemView.textDeaths.text = numberFormatter.format(generalInfo.deaths)
    }
    
    private fun updateChip(optionType: OptionType) {
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