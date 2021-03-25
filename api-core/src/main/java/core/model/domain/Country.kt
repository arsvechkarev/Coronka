package core.model.domain

/**
 * Country object for domain layer
 */
data class Country(
  val id: String,
  val name: String,
  val slug: String,
  val iso2: String,
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val newConfirmed: Int,
  val newDeaths: Int,
)