package core.model

import core.recycler.DisplayableItem

class Country(
  val id: Int,
  val name: String,
  val iso2: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val latitude: Double,
  val longitude: Double
)

data class DisplayableCountry(
  val name: String,
  val amount: Number
) : DisplayableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.toFloat().compareTo(other.amount.toFloat())
  }
}