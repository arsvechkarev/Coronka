package core.model.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * General information about cases in the world
 */
@Keep
data class GeneralInfo(
  @SerializedName("cases") val confirmed: Int,
  @SerializedName("deaths") val deaths: Int,
  @SerializedName("recovered") val recovered: Int
)