package core.model

data class CountryInfo(
  val countryId: Int,
  val countryName: String,
  val confirmed: String,
  val deaths: String,
  val recovered: String
)