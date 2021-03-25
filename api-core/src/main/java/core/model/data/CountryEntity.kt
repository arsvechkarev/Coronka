package core.model.data

import com.google.gson.annotations.SerializedName

/**
 * Country entity from network (data layer)
 *
 * @param slug Country name in lowercase, like "germany", "france", ect.
 * @param iso2 Country ISO code, like "US", "FR", "UK", etc
 * @param date Date in format **2021-03-04T11:51:53.806Z**
 */
data class CountryEntity(
  @SerializedName("ID") val id: String,
  @SerializedName("Country") val name: String,
  @SerializedName("Slug") val slug: String,
  @SerializedName("CountryCode") val iso2: String,
  @SerializedName("TotalConfirmed") val confirmed: Int,
  @SerializedName("TotalRecovered") val recovered: Int,
  @SerializedName("TotalDeaths") val deaths: Int,
  @SerializedName("NewConfirmed") val newConfirmed: Int,
  @SerializedName("NewRecovered") val newRecovered: Int,
  @SerializedName("NewDeaths") val newDeaths: Int,
  @SerializedName("Date") val date: String,
)