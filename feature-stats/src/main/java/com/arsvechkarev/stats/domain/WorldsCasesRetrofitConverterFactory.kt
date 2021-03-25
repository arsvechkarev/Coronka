package com.arsvechkarev.stats.domain

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import core.JsonConverter
import core.model.data.WorldCasesInfo
import core.model.ui.DailyCase
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class WorldsCasesRetrofitConverterFactory(
  private val converter: JsonConverter<WorldCasesInfo>
) : Converter.Factory() {
  
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, WorldCasesInfo>? {
    val expectedType = TypeToken.get(WorldCasesInfo::class.java).type
    if (type != expectedType) {
      return null
    }
    return Converter { converter.convert(it.string()) }
  }
}

class WorldInfoJsonConverter : JsonConverter<WorldCasesInfo> {
  
  override fun convert(json: String): WorldCasesInfo {
    val dailyCases = ArrayList<DailyCase>()
    val array = JsonParser.parseString(json).asJsonArray
    for (i in array.size() - MAX_CASES until array.size()) {
      val obj = array.get(i).asJsonObject
      val cases = obj.get(CONFIRMED).asInt
      val date = obj.get(DATE).asString
      val dailyCase = DailyCase(cases, date)
      dailyCases.add(dailyCase)
    }
    val newDailyCases = toNewDailyCases(dailyCases)
    dailyCases.removeFirst()
    return WorldCasesInfo(dailyCases, newDailyCases)
  }
  
  private fun toNewDailyCases(list: List<DailyCase>): List<DailyCase> {
    val dailyCases = ArrayList<DailyCase>()
    for (i in 1 until list.size) {
      val curr = list[i]
      val prev = list[i - 1]
      val diff = curr.cases - prev.cases
      dailyCases.add(DailyCase(diff, curr.date))
    }
    return dailyCases
  }
  
  private companion object {
    
    const val CONFIRMED = "Confirmed"
    const val DATE = "Date"
    const val MAX_CASES = 181 // Half a year + 1 day to calculate new cases properly
  }
}