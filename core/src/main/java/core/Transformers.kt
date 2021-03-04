package core

import core.model.GeneralInfo
import org.json.JSONObject

private const val CONFIRMED = "cases"
private const val RECOVERED = "recovered"
private const val DEATHS = "deaths"

fun String.toGeneralInfo(): GeneralInfo {
  val obj = JSONObject(this)
  return GeneralInfo(
    obj.getInt(CONFIRMED),
    obj.getInt(RECOVERED),
    obj.getInt(DEATHS)
  )
}