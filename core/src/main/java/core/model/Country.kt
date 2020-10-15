package core.model

import core.recycler.DifferentiableItem

/**
 * Basic country information
 *
 * @param slug Country name in lowercase, like "germany", "france", ect.
 * @param iso2 Country ISO code, like "US", "FR", "UK", etc
 */
data class Country(
  val id: Int,
  val name: String,
  val slug: String,
  val iso2: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int
)

/** Country to be displayed on a map */
data class CountryOnMap(
  val country: Country,
  val location: Location
)

/** Country with meta information: population and world region  */
data class CountryMetaInfo(
  val iso2: String,
  val population: Int,
  val worldRegion: String
)

/** Country for displaying in a list */
data class DisplayableCountry(
  val name: String,
  val amount: Number,
  val amountString: String
) : DifferentiableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number.toString()
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.toFloat().compareTo(other.amount.toFloat())
  }
}