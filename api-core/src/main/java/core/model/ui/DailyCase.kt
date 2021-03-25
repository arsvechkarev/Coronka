package core.model.ui

/**
 * Coronavirus cases on a particular date
 *
 * @param cases Number of cases
 * @param date Date in format "MMM DD", like "Sep 14", "Jul 30", etc.
 */
class DailyCase(
  val cases: Int,
  val date: String
)