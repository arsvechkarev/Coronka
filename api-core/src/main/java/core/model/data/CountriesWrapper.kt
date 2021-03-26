package core.model.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Countries wrapper for list of countries from network
 */
@Keep
data class CountriesWrapper(
  @SerializedName("Countries") val countries: List<CountryEntity>
)