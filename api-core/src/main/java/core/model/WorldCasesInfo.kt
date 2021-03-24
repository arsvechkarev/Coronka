package core.model

/** Information about world cases for stats screen
 * */
class WorldCasesInfo(
  val totalDailyCases: List<DailyCase>,
  val newDailyCases: List<DailyCase>,
)