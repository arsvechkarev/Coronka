package core.model

class WorldCasesInfo(
  val generalInfo: GeneralInfo,
  val totalDailyCases: List<DailyCase>,
  val newDailyCases: List<DailyCase>,
)