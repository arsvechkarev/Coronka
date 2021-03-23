package core.model

import com.google.gson.annotations.SerializedName

/**
 * Coronavirus cases on a particular date
 *
 * @param cases Number of cases
 * @param date Date in format "MMM DD", like "Sep 14", "Jul 30", etc.
 */
data class DailyCase(
  @SerializedName("Confirmed") val cases: Int,
  @SerializedName("Date") val date: String
)