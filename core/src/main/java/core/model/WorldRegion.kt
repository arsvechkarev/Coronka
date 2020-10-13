package core.model

/** World region in which a country is located */
enum class WorldRegion(val letters: String? = null) {
  WORLDWIDE,
  ASIA("AS"),
  AFRICA("AF"),
  EUROPE("EU"),
  NORTH_AMERICA("NA"),
  SOUTH_AMERICA("SA"),
  OCEANIA("OC")
}