package core.model

/**
 * Total information about cases in the world
 */
data class GeneralInfo(
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int
)