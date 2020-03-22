package core.model

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
      "${it.countryName}: ${it.countryId}|${it.countryCode}|${it.confirmed}" +
          "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}"
    )
  }
}