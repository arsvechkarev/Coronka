package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.rankings.R
import com.arsvechkarev.views.Chip
import core.model.OptionType
import core.model.WorldRegion

class ChipHelper(
  optionsTypeChips: ChipGroup,
  worldRegionChips: ChipGroup,
  private val onNewChipSelected: (OptionType, WorldRegion) -> Unit,
  private val onWorldRegionChipSelected: (Chip) -> Unit,
  private val onOptionTypeChipSelected: (Chip) -> Unit,
) {
  
  private var currentOptionType = OptionType.CONFIRMED
  private var currentWorldRegion = WorldRegion.WORLDWIDE
  
  init {
    optionsTypeChips.onNewChipSelected = { chip ->
      onOptionTypeChipSelected(chip)
      when (chip.id) {
        R.id.chipConfirmed -> notifyClick(OptionType.CONFIRMED)
        R.id.chipRecovered -> notifyClick(OptionType.RECOVERED)
        R.id.chipDeaths -> notifyClick(OptionType.DEATHS)
        R.id.chipPercentByCountry -> notifyClick(OptionType.PERCENT_BY_COUNTRY)
        R.id.chipDeathRate -> notifyClick(OptionType.DEATH_RATE)
      }
    }
    worldRegionChips.onNewChipSelected = { chip ->
      onWorldRegionChipSelected(chip)
      when (chip.id) {
        R.id.chipWorldwide -> notifyClick(WorldRegion.WORLDWIDE)
        R.id.chipEurope -> notifyClick(WorldRegion.EUROPE)
        R.id.chipAsia -> notifyClick(WorldRegion.ASIA)
        R.id.chipAfrica -> notifyClick(WorldRegion.AFRICA)
        R.id.chipNorthAmerica -> notifyClick(WorldRegion.NORTH_AMERICA)
        R.id.chipOceania -> notifyClick(WorldRegion.OCEANIA)
        R.id.chipSouthAmerica -> notifyClick(WorldRegion.SOUTH_AMERICA)
      }
    }
  }
  
  private fun notifyClick(optionType: OptionType) {
    currentOptionType = optionType
    onNewChipSelected(currentOptionType, currentWorldRegion)
  }
  
  private fun notifyClick(worldRegion: WorldRegion) {
    currentWorldRegion = worldRegion
    onNewChipSelected(currentOptionType, currentWorldRegion)
  }
}