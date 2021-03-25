package core.model.data

import core.model.ui.DailyCase

/** Information about world cases for stats screen
 * */
class WorldCasesInfo(
  val totalDailyCases: List<DailyCase>,
  val newDailyCases: List<DailyCase>,
)