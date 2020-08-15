package core.model

enum class WorldRegion(val letters: String? = null) {
  WORLDWIDE,
  ASIA("AS"),
  AFRICA("AF"),
  EUROPE("EU"),
  NORTH_AMERICA("NA"),
  SOUTH_AMERICA("SA"),
  OCEANIA("OC")
}