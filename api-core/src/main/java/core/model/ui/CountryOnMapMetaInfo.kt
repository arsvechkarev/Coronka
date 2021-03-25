package core.model.ui

import core.model.data.Location

/** Country to be displayed on a map */
data class CountryOnMapMetaInfo(
  val id: String,
  val confirmed: Int,
  val location: Location
)