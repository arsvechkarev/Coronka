package core.model

data class CountryInfo(
  val countryId: Int,
  val countryName: String,
  val confirmed: String,
  val deaths: String,
  val recovered: String,
  val latitude: String,
  val longitude: String
)

fun List<CountryInfo>.print(prefix: String = "countr") {
  forEach {
    println(
      "${it.countryId}|${it.countryName}|${it.confirmed}" +
          "|${it.deaths}|${it.recovered}|${it.latitude}|${it.longitude}"
    )
  }
}