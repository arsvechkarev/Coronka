package core.model

import core.recycler.DisplayableItem

data class Country(
  val countryId: Int,
  val countryName: String,
  val countryCode: String,
  val confirmed: String,
  val deaths: String,
  val recovered: String,
  val latitude: String,
  val longitude: String
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
  val number: Int,
  val percent: Float,
  val color: Int
) : DisplayableItem {
  override val id = name
  override val type = TYPE_COUNTRY_INFO
}