package core.model

/** Type of information about a country used for filtering */
enum class OptionType {
  CONFIRMED,
  RECOVERED,
  DEATHS,
  DEATH_RATE,
  PERCENT_IN_COUNTRY
}