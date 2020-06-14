package core.extenstions

import io.reactivex.Observable


fun <T> Observable<T>.startWithIf(value: T, condition: Boolean): Observable<T> {
  if (condition) {
    return this.startWith(value)
  } else {
    return this
  }
}