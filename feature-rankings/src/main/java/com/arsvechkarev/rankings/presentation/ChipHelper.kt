package com.arsvechkarev.rankings.presentation

import base.resources.Colors
import com.arsvechkarev.rankings.R
import core.model.OptionType
import core.model.WorldRegion

class ChipHelper(
  private val optionsTypeChips: ChipGroup,
  private val worldRegionChips: ChipGroup,
  private val onWorldRegionSelected: (WorldRegion) -> Unit,
  private val onOptionTypeSelected: (OptionType) -> Unit,
) {
  
  fun setSelectedWorldRegion(worldRegion: WorldRegion) {
    worldRegionChips.setSelectedChipById(getWorldRegionChipId(worldRegion))
  }
  
  fun setSelectedOptionType(optionType: OptionType) {
    optionsTypeChips.setSelectedChipById(getOptionTypeChipId(optionType))
  }
  
  init {
    worldRegionChips.onNewChipSelected = { chip ->
      val currentWorldRegion: WorldRegion
      when (chip.id) {
        R.id.chipWorldwide -> currentWorldRegion = WorldRegion.WORLDWIDE
        R.id.chipEurope -> currentWorldRegion = WorldRegion.EUROPE
        R.id.chipAsia -> currentWorldRegion = WorldRegion.ASIA
        R.id.chipAfrica -> currentWorldRegion = WorldRegion.AFRICA
        R.id.chipNorthAmerica -> currentWorldRegion = WorldRegion.NORTH_AMERICA
        R.id.chipOceania -> currentWorldRegion = WorldRegion.OCEANIA
        R.id.chipSouthAmerica -> currentWorldRegion = WorldRegion.SOUTH_AMERICA
        else -> throw IllegalStateException("Unknown world region chip $chip")
      }
      onWorldRegionSelected(currentWorldRegion)
    }
    optionsTypeChips.onNewChipSelected = { chip ->
      val currentOptionType: OptionType
      when (chip.id) {
        R.id.chipConfirmed -> currentOptionType = OptionType.CONFIRMED
        R.id.chipRecovered -> currentOptionType = OptionType.RECOVERED
        R.id.chipDeaths -> currentOptionType = OptionType.DEATHS
        R.id.chipPercentInCountry -> currentOptionType = OptionType.PERCENT_IN_COUNTRY
        R.id.chipDeathRate -> currentOptionType = OptionType.DEATH_RATE
        else -> throw IllegalStateException("Unknown option type chip $chip")
      }
      onOptionTypeSelected(currentOptionType)
    }
  }
}

fun getWorldRegionChipId(worldRegion: WorldRegion) = when (worldRegion) {
  WorldRegion.WORLDWIDE -> R.id.chipWorldwide
  WorldRegion.ASIA -> R.id.chipAsia
  WorldRegion.AFRICA -> R.id.chipAfrica
  WorldRegion.EUROPE -> R.id.chipEurope
  WorldRegion.NORTH_AMERICA -> R.id.chipNorthAmerica
  WorldRegion.SOUTH_AMERICA -> R.id.chipSouthAmerica
  WorldRegion.OCEANIA -> R.id.chipOceania
}

fun getOptionTypeChipId(optionType: OptionType) = when (optionType) {
  OptionType.CONFIRMED -> R.id.chipConfirmed
  OptionType.RECOVERED -> R.id.chipRecovered
  OptionType.DEATHS -> R.id.chipDeaths
  OptionType.DEATH_RATE -> R.id.chipDeathRate
  OptionType.PERCENT_IN_COUNTRY -> R.id.chipPercentInCountry
}

fun getTextIdForWorldRegion(worldRegion: WorldRegion) = when (worldRegion) {
  WorldRegion.WORLDWIDE -> R.string.text_worldwide
  WorldRegion.ASIA -> R.string.text_asia
  WorldRegion.AFRICA -> R.string.text_africa
  WorldRegion.EUROPE -> R.string.text_europe
  WorldRegion.NORTH_AMERICA -> R.string.text_north_america
  WorldRegion.SOUTH_AMERICA -> R.string.text_south_america
  WorldRegion.OCEANIA -> R.string.text_oceania
}

fun getTextIdForOptionType(optionType: OptionType) = when (optionType) {
  OptionType.CONFIRMED -> R.string.text_confirmed
  OptionType.RECOVERED -> R.string.text_recovered
  OptionType.DEATHS -> R.string.text_deaths
  OptionType.DEATH_RATE -> R.string.text_death_rate
  OptionType.PERCENT_IN_COUNTRY -> R.string.text_percent_in_country
}

fun getColorForOptionType(optionType: OptionType) = when (optionType) {
  OptionType.CONFIRMED -> Colors.Confirmed
  OptionType.RECOVERED -> Colors.Recovered
  OptionType.DEATHS -> Colors.Deaths
  OptionType.DEATH_RATE -> Colors.DeathRate
  OptionType.PERCENT_IN_COUNTRY -> Colors.PercentByCountry
}