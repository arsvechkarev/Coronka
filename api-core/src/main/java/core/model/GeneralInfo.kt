package core.model

import com.google.gson.annotations.SerializedName

/**
 * Total information about cases in the world
 */
data class GeneralInfo(
  @SerializedName("cases") val confirmed: Int,
  @SerializedName("deaths") val deaths: Int,
  @SerializedName("recovered") val recovered: Int
)