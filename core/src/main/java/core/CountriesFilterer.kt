package core

import core.model.Country
import core.model.CountryMetaInfo
import core.model.OptionType
import core.model.WorldRegion
import core.recycler.DifferentiableItem

/**
 * Performs filtering of countries
 *
 * @see CountriesFiltererImpl
 */
interface CountriesFilterer {
  
  /**
   * Performs first filtering. After that call [countries] and [countriesMetaInfo] should be stored
   * and calling [filter] without mentioned values should be possible
   *
   * @return List of countries filtered according to [worldRegion] and [optionType]
   */
  fun filterInitial(
    countries: List<Country>,
    countriesMetaInfo: Map<String, CountryMetaInfo>,
    worldRegion: WorldRegion,
    optionType: OptionType
  ): List<DifferentiableItem>
  
  /**
   * Performs countries filtering. Do not forget to call [filterInitial] before calling this
   * function
   *
   * @return List of countries filtered according to [worldRegion] and [optionType]
   */
  fun filter(worldRegion: WorldRegion, optionType: OptionType): List<DifferentiableItem>
}