package com.arsvechkarev.common.domain.transformers

import com.google.gson.JsonParser
import core.model.DailyCase

object WorldCasesInfoTransformer {
  
  private const val CONFIRMED = "Confirmed"
  private const val DATE = "Date"
  private const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
  
  fun toDailyCases(json: String): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    val array = JsonParser().parse(json).asJsonArray
    for (i in array.size() - MAX_CASES until array.size()) {
      val obj = array.get(i).asJsonObject
      val cases = obj.get(CONFIRMED).asInt
      val date = obj.get(DATE).asString
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