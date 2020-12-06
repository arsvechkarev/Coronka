package core.extenstions

import core.MIN_NETWORK_DELAY
import core.REQUEST_TIMEOUT
import core.concurrency.Schedulers
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.withNetworkDelay(schedulers: Schedulers): Observable<T> {
  return delay(MIN_NETWORK_DELAY, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}

fun <T> Observable<T>.withRequestTimeout(): Observable<T> {
  return timeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
}