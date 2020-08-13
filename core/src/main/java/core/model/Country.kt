package core.model

import core.recycler.SortableDisplayableItem

class Country(
  val id: Int,
  val name: String,
  val slug: String,
  val iso2: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int
)

class CountryOnMap(
  val iso2: String,
  val color: Int
)

data class DisplayableCountry(
  val name: String,
  val amount: Number
) : SortableDisplayableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.toFloat().compareTo(other.amount.toFloat())
  }
}