package core.extenstions

import core.RxConfigurator
import core.Schedulers
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.withRetry(): Observable<T> {
  val retryCount = RxConfigurator.retryCount
  if (retryCount == 0L) return this
  return retry(retryCount)
}

fun <T> Observable<T>.withNetworkDelay(schedulers: Schedulers): Observable<T> {
  val delay = RxConfigurator.networkDelay
  return delay(delay, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}

fun <T> Observable<T>.withRequestTimeout(): Observable<T> {
  return timeout(RxConfigurator.requestTimeout, TimeUnit.MILLISECONDS)
}