package com.arsvechkarev.coronka.fakes

import core.WebApi
import io.reactivex.Single

object FakeWebApi : WebApi {
  
  override fun request(url: String): Single<String> = Single.error(Throwable())
}