package core.extenstions

import core.MIN_NETWORK_DELAY
import core.REQUEST_TIMEOUT
import core.concurrency.Schedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.withNetworkDelay(schedulers: Schedulers): Observable<T> {
  return delay(MIN_NETWORK_DELAY, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}

fun <T> Observable<T>.withRequestTimeout(): Observable<T> {
  return timeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
}

fun <T> Single<T>.withNetworkDelay(schedulers: Schedulers): Single<T> {
  return delay(MIN_NETWORK_DELAY, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}

fun <T> Single<T>.withRequestTimeout(): Single<T> {
  return timeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
}

fun Completable.withRequestTimeout(): Completable {
  return timeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
}

fun Completable.withNetworkDelay(schedulers: Schedulers): Completable {
  return delay(MIN_NETWORK_DELAY, TimeUnit.MILLISECONDS, schedulers.computation(), true)
}
