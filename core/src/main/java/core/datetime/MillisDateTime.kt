package core.datetime

import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Represents date and time with milliseconds since epoch and allows for calculating difference
 * between two instances
 */
class MillisDateTime(
  val millis: Long
) {
  
  /**
   * Returns the difference with [other] in a given [timeUnit]
   *
   * Note: the difference is absolute
   */
  fun differenceWith(other: MillisDateTime, timeUnit: TimeUnit): Long {
    return timeUnit.convert(abs(millis - other.millis), TimeUnit.MILLISECONDS)
  }
  
  companion object {
  
    fun current(): MillisDateTime {
      return MillisDateTime(Date().time)
    }
  
    fun ofMillis(millis: Long) = MillisDateTime(millis)
  }
}