package core.transformers

import core.model.DailyCase
import org.json.JSONArray

object WorldCasesInfoTransformer {
  
  private const val CONFIRMED = "Confirmed"
  private const val DATE = "Date"
  private const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
  
  fun toDailyCases(json: String): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    val array = JSONArray(json)
    for (i in array.length() - MAX_CASES until array.length()) {
      val obj = array.getJSONObject(i)
      val cases = obj.getInt(CONFIRMED)
      val date = obj.getString(DATE)
      val dailyCase = DailyCase(cases, date)
      dailyCases.add(dailyCase)
    }
    return dailyCases
  }
  
  
  fun toNewDailyCases(list: List<DailyCase>): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    for (i in 1 until list.size) {
      val curr = list[i]
      val prev = list[i - 1]
      val diff = curr.cases - prev.cases
      dailyCases.add(DailyCase(diff, curr.date))
    }
    return dailyCases
  }
}