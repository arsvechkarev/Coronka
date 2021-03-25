package core.model.data

import com.google.gson.annotations.SerializedName

class DailyCaseEntity(
  @SerializedName("Confirmed") val cases: Int,
  @SerializedName("Recovered") val recovered: Int,
  @SerializedName("Deaths") val deaths: Int,
  @SerializedName("Date") val date: String
)