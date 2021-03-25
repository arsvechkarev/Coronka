package core.model.data

/** Country with meta information: population and world region  */
data class CountryMetaInfo(
  val iso2: String,
  val population: Int,
  val worldRegion: String
)