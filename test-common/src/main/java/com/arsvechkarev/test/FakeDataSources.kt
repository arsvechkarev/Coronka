package com.arsvechkarev.test

import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.common.domain.TotalInfoDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSource
import com.arsvechkarev.common.repository.CountriesMetaInfoRepository
import core.model.Country
import core.model.CountryMetaInfo
import core.model.DailyCase
import core.model.GeneralInfo
import core.model.Location
import core.model.TotalInfo
import core.model.WorldCasesInfo
import core.model.WorldRegion.ASIA
import core.model.WorldRegion.EUROPE
import core.model.WorldRegion.NORTH_AMERICA
import core.model.WorldRegion.OCEANIA
import core.model.WorldRegion.SOUTH_AMERICA
import io.reactivex.Single
import java.net.UnknownHostException

val FakeGeneralInfo = GeneralInfo(115887454, 2573769, 91562289)

val FakeMetaInfoMap = mapOf(
  "US" to CountryMetaInfo("US", 320_000_000, NORTH_AMERICA.letters!!),
  "CA" to CountryMetaInfo("CA", 40_000_000, NORTH_AMERICA.letters!!),
  "UK" to CountryMetaInfo("UK", 62_000_000, EUROPE.letters!!),
  "FR" to CountryMetaInfo("FR", 31_000_000, EUROPE.letters!!),
  "DE" to CountryMetaInfo("DE", 53_000_000, EUROPE.letters!!),
  "CN" to CountryMetaInfo("CN", 1232_000_000, ASIA.letters!!),
  "IN" to CountryMetaInfo("IN", 1124_000_000, ASIA.letters!!),
  "BR" to CountryMetaInfo("BR", 351_000_000, SOUTH_AMERICA.letters!!),
  "AU" to CountryMetaInfo("AU", 23_000_000, OCEANIA.letters!!),
)

val FakeLocationsMap = mapOf(
  "US" to Location(2.36, 2.0),
  "CA" to Location(1.898, 6.2),
  "UK" to Location(3.0, 3.1),
  "FR" to Location(12.0, 4.86),
  "DE" to Location(9.0, 3.34),
  "CN" to Location(6.2, 1.0),
  "IN" to Location(7.0, 4.9),
  "BR" to Location(2.59, 12.87),
  "AU" to Location(8.0, 2.13),
)

val FakeCountries = listOf(
  Country(0, "USA", "united states", "US",
    25_000_000, 200_000, 12_000_000, 23_000, 2_230),
  Country(1, "Canada", "canada", "CA",
    2_000_000, 20_000, 1_500_045, 8_100, 400),
  Country(2, "United Kingdom", "united kingdom", "UK",
    3_210_000, 200_000, 2_062_000, 15_060, 650),
  Country(3, "France", "france", "FR",
    1_500_000, 15_600, 1_221_000, 7_300, 306),
  Country(4, "Germany", "germany", "DE",
    1_200_000, 12_460, 950_600, 6_000, 390),
  Country(5, "China", "China", "CN",
    750_000, 22_690, 611_000, 3_200, 90),
  Country(6, "India", "india", "IN",
    8_000_000, 120_030, 6_260_000, 213_000, 19_230),
  Country(7, "Brazil", "brazil", "BR",
    6_000_000, 110_200, 4_230_000, 210_600, 23_230),
  Country(8, "Australia", "australia", "AU",
    620_000, 18_500, 450_000, 2_000, 236),
)

val FakeTotalInfo = TotalInfo(FakeCountries, FakeGeneralInfo)

val FakeTotalDailyCases = listOf<DailyCase>(
  DailyCase(650_000, "March 1"),
  DailyCase(685_000, "March 2"),
  DailyCase(920_000, "March 3"),
  DailyCase(946_000, "March 4"),
  DailyCase(999_000, "March 5"),
  DailyCase(1_030_000, "March 6")
)

val FakeNewDailyCases = listOf<DailyCase>(
  DailyCase(650_000, "March 1"),
  DailyCase(685_000, "March 2"),
  DailyCase(920_000, "March 3"),
  DailyCase(946_000, "March 4"),
  DailyCase(999_000, "March 5"),
  DailyCase(1_030_000, "March 6")
)

class FakeCountriesMetaInfoDataSource : CountriesMetaInfoRepository {
  
  override fun getCountriesMetaInfoSync(): Map<String, CountryMetaInfo> {
    return FakeMetaInfoMap
  }
  
  override fun getLocationsMap(): Single<Map<String, Location>> {
    return Single.just(FakeLocationsMap)
  }
}

class FakeGeneralInfoDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : GeneralInfoDataSource {
  
  private var retryCount = 0
  
  override fun requestGeneralInfo() = Single.create<GeneralInfo> { emitter ->
    if (retryCount < totalRetryCount) {
      retryCount++
      emitter.onError(errorFactory())
      return@create
    }
    emitter.onSuccess(FakeGeneralInfo)
  }
}

class FakeTotalInfoDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : TotalInfoDataSource {
  
  private var retryCount = 0
  
  override fun requestTotalInfo() = Single.create<TotalInfo> { emitter ->
    if (retryCount < totalRetryCount) {
      retryCount++
      emitter.onError(errorFactory())
      return@create
    }
    emitter.onSuccess(FakeTotalInfo)
  }
}

class FakeWorldCasesInfoDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : WorldCasesInfoDataSource {
  
  private var retryCount = 0
  
  override fun requestWorldDailyCases(): Single<WorldCasesInfo> = Single.create { emitter ->
    if (retryCount < totalRetryCount) {
      retryCount++
      emitter.onError(errorFactory())
      return@create
    }
    emitter.onSuccess(WorldCasesInfo(FakeTotalDailyCases, FakeNewDailyCases))
  }
}