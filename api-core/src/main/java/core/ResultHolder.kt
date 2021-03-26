@file:Suppress("UNCHECKED_CAST")

package core

/**
 * Result class that encapsulates success or failure
 */
class ResultHolder<T> private constructor(@PublishedApi internal val value: Any) {
  
  val isSuccess get() = value !is Throwable
  
  val isFailure get() = value is Throwable
  
  val exception: Throwable get() = value as Throwable
  
  fun getOrThrow(): T {
    if (isSuccess) return value as T else throw value as Throwable
  }
  
  companion object {
    
    fun <T : Any> success(value: T): ResultHolder<T> {
      require(value !is Throwable) { "Success value can't be throwable" }
      return ResultHolder(value)
    }
    
    fun <T> failure(value: Throwable): ResultHolder<T> = ResultHolder(value)
  }
}

inline fun <R, T> ResultHolder<T>.fold(
  onSuccess: (value: T) -> R,
  onFailure: (exception: Throwable) -> R
): R {
  return if (isSuccess) onSuccess(value as T) else onFailure(value as Throwable)
}
