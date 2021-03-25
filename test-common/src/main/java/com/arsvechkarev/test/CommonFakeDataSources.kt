package com.arsvechkarev.test

import com.arsvechkarev.common.domain.CountriesDataSource
import com.arsvechkarev.common.domain.WorldCasesInfoDataSource
import core.model.data.CountriesWrapper
import core.model.data.CountryEntity
import core.model.data.WorldCasesInfo
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.DailyCase
import io.reactivex.Single
import java.net.UnknownHostException

val FakeCountryEntities = listOf(
  CountryEntity("0", "USA", "united states", "US", 25_000_000,
    200_000, 12_000_000, 23_000, 2_230, 500, ""),
  CountryEntity("1", "Canada", "canada", "CA", 2_000_000,
    20_000, 1_500_045, 8_100, 400, 100, ""),
  CountryEntity("2", "United Kingdom", "united kingdom", "UK", 3_210_000,
    200_000, 2_062_000, 15_060, 650, 150, ""),
  CountryEntity("3", "France", "france", "FR", 1_500_000,
    15_600, 1_221_000, 7_300, 306, 75, ""),
  CountryEntity("4", "Germany", "germany", "DE", 1_200_000,
    12_460, 950_600, 6_000, 390, 80, ""),
  CountryEntity("5", "China", "China", "CN", 750_000,
    22_690, 611_000, 3_200, 90, 15, ""),
  CountryEntity("6", "India", "india", "IN", 8_000_000,
    120_030, 6_260_000, 213_000, 19_230, 4500, ""),
  CountryEntity("7", "Brazil", "brazil", "BR", 6_000_000,
    110_200, 4_230_000, 210_600, 23_230, 5200, ""),
  CountryEntity("8", "Australia", "australia", "AU", 620_000,
    18_500, 450_000, 2_000, 236, 40, ""),
)

val FakeCountries = CountryEntitiesToCountriesMapper().map(FakeCountryEntities)

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

class FakeCountriesDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : CountriesDataSource {
  
  private var retryCount = 0
  
  override fun requestCountries() = Single.create<CountriesWrapper> { emitter ->
    if (retryCount < totalRetryCount) {
      retryCount++
      emitter.onError(errorFactory())
      return@create
    }
    emitter.onSuccess(CountriesWrapper(FakeCountryEntities))
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