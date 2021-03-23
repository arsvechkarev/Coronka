package base.extensions

import config.RxConfigurator
import core.Schedulers
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.withRetry(): Observable<T> {
  return retry(RxConfigurator.retryCount)
}

fun <T> Observable<T>.withNetworkDelay(schedulers: Schedulers): Observable<T> {
  val delay = RxConfigurator.networkDelay
  return delay(delay, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}

fun <T> Observable<T>.withRequestTimeout(): Observable<T> {
  return timeout(RxConfigurator.requestTimeout, TimeUnit.MILLISECONDS)
}

fun <T> Observable<T>.startWithIf(element: T, condition: () -> Boolean): Observable<T> {
  return if (condition()) startWith(element) else this
}