package core.model

import core.recycler.DisplayableItem

data class Country(
  val countryId: Int,
  val countryName: String,
  val countryCode: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val latitude: Double,
  val longitude: Double
)

fun List<Country>.print(prefix: String = "country") {
  forEach {
    println(
      "$prefix -- ${it.countryName}: ${it.countryId}|${it.countryCode}|${it.confirmed}" +
          "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}"
    )
  }
}

data class DisplayableCountry(
  val name: String,
  val amount: Int
) : DisplayableItem, Comparable<DisplayableCountry> {
  
  var number: Int = 0
  
  override val id = number
  
  override val type = TYPE_COUNTRY_INFO
  
  override fun compareTo(other: DisplayableCountry): Int {
    return amount.compareTo(other.amount)
  }
}