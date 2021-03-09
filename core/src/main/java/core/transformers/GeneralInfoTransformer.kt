package core.transformers

import core.model.GeneralInfo
import org.json.JSONObject

object GeneralInfoTransformer {
  
  private const val CONFIRMED = "cases"
  private const val RECOVERED = "recovered"
  private const val DEATHS = "deaths"
  
  fun toGeneralInfo(json: String): GeneralInfo {
    val obj = JSONObject(json)
    return GeneralInfo(
      obj.getInt(CONFIRMED),
      obj.getInt(DEATHS),
      obj.getInt(RECOVERED),
    )
  }
}