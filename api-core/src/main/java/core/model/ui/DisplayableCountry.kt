package core.model.ui

import api.recycler.DifferentiableItem
import core.model.domain.Country

/** Country for displaying in a list */
data class DisplayableCountry(
  val name: String,
  val amount: Number,
  val amountString: String,
  val country: Country
) : DifferentiableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number.toString()
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.toFloat().compareTo(other.amount.toFloat())
  }
}