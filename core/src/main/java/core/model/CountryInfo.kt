package core.model

data class CountryInfo(
  val countryId: Int,
  val countryName: String,
  val countryCode: String,
  val confirmed: String,
  val deaths: String,
  val recovered: String,
  val latitude: String,
  val longitude: String
)

fun List<CountryInfo>.print(prefix: String = "country") {
  forEach {
    println(
      "${it.countryName}: ${it.countryId}|${it.countryCode}|${it.confirmed}" +
          "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}"
    )
  }
}