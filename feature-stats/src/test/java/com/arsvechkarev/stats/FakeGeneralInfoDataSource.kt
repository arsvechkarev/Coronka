package com.arsvechkarev.stats

import core.model.data.GeneralInfo
import io.reactivex.Single
import java.net.UnknownHostException

val FakeGeneralInfo = GeneralInfo(115887454, 2573769, 91562289)

class FakeGeneralInfoDataSource(
  private val totalRetryCount: Int = 0,
  private val errorFactory: () -> Throwable = { UnknownHostException() }
) : com.arsvechkarev.stats.domain.GeneralInfoDataSource {
  
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