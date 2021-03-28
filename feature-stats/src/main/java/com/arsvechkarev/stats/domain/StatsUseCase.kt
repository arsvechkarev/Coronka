package com.arsvechkarev.stats.domain

import base.extensions.withNetworkDelay
import base.extensions.withRequestTimeout
import base.extensions.withRetry
import com.arsvechkarev.featurescommon.domain.WorldCasesInfoDataSource
import core.model.data.GeneralInfo
import core.model.data.MainStatistics
import core.model.data.WorldCasesInfo
import core.rx.Schedulers
import io.reactivex.Observable
import io.reactivex.Single

class StatsUseCase(
  private val generalInfoDataSource: GeneralInfoDataSource,
  private val worldCasesInfoDataSource: WorldCasesInfoDataSource,
  private val schedulers: Schedulers
) {
  
  fun getMainStatistics(): Observable<MainStatistics> {
    return Single.zip(
      generalInfoDataSource.requestGeneralInfo()
          .subscribeOn(schedulers.io())
          .map(Result.Companion::success)
          .onErrorReturn(Result.Companion::failure),
      worldCasesInfoDataSource.requestWorldDailyCases()
          .subscribeOn(schedulers.io())
          .map(Result.Companion::success)
          .onErrorReturn(Result.Companion::failure),
      { info, cases -> mapToResult(info, cases)() }
    ).toObservable()
        .withNetworkDelay(schedulers)
        .flatMap { result ->
          result.fold(
            onSuccess = { worldCasesInfo -> Observable.just(worldCasesInfo) },
            onFailure = { throwable -> Observable.error(throwable) }
          )
        }
        .withRetry()
        .withRequestTimeout()
  }
  
  // Result cannot be used as a return type, so returning '() -> Result' instead.
  // See https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md#limitations for more info
  private fun mapToResult(
    resultInfo: Result<GeneralInfo>,
    resultCases: Result<WorldCasesInfo>
  ): () -> Result<MainStatistics> {
    val generalInfo = resultInfo.getOrElse { return { Result.failure(it) } }
    val worldCasesInfo = resultCases.getOrElse { return { Result.failure(it) } }
    return {
      Result.success(MainStatistics(generalInfo, worldCasesInfo))
    }
  }
}