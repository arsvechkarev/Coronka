package com.arsvechkarev.coronka

import com.arsvechkarev.rankings.domain.CountriesMetaInfoDataSource
import core.model.WorldRegion
import core.model.data.CountryMetaInfo
import io.reactivex.Single

val FakeMetaInfoMap = mapOf(
  "US" to CountryMetaInfo("US", 320_000_000, WorldRegion.NORTH_AMERICA.letters!!),
  "CA" to CountryMetaInfo("CA", 40_000_000, WorldRegion.NORTH_AMERICA.letters!!),
  "UK" to CountryMetaInfo("UK", 62_000_000, WorldRegion.EUROPE.letters!!),
  "FR" to CountryMetaInfo("FR", 31_000_000, WorldRegion.EUROPE.letters!!),
  "DE" to CountryMetaInfo("DE", 53_000_000, WorldRegion.EUROPE.letters!!),
  "CN" to CountryMetaInfo("CN", 1232_000_000, WorldRegion.ASIA.letters!!),
  "IN" to CountryMetaInfo("IN", 1124_000_000, WorldRegion.ASIA.letters!!),
  "BR" to CountryMetaInfo("BR", 351_000_000, WorldRegion.SOUTH_AMERICA.letters!!),
  "AU" to CountryMetaInfo("AU", 23_000_000, WorldRegion.OCEANIA.letters!!),
)

class FakeCountriesMetaInfoDataSource : CountriesMetaInfoDataSource {
  
  override fun getCountriesMetaInfo(): Single<Map<String, CountryMetaInfo>> {
    return Single.just(FakeMetaInfoMap)
  }
}