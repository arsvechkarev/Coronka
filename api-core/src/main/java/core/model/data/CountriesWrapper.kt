package core.model.data

import com.google.gson.annotations.SerializedName

/**
 * Countries wrapper for list of countries from network
 */
data class CountriesWrapper(
  @SerializedName("Countries") val countries: List<CountryEntity>
)