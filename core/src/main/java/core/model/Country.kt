package core.model

import core.recycler.DisplayableItem

data class Country(
  val id: Int,
  val name: String,
  val iso2: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val latitude: Double,
  val longitude: Double
)

fun List<Country>.print(prefix: String = "country") {
  forEach {
    println(
      "$prefix -- ${it.name}: ${it.id}|${it.iso2}|${it.confirmed}" +
          "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}"
    )
  }
}

data class DisplayableCountry(
  val name: String,
  val amount: Number
) : DisplayableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number
  
  override val type = TYPE_COUNTRY_INFO
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.toFloat().compareTo(other.amount.toFloat())
  }
}