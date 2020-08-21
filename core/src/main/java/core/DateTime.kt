package core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Represents date and time with milliseconds since epoch and allows for calculating difference
 * between two instances
 */
class DateTime(
  val millis: Long
) {
  
  /**
   * Returns the difference with [other] in a given [timeUnit]
   *
   * Note: the difference is absolute
   */
  fun differenceWith(other: DateTime, timeUnit: TimeUnit): Long {
    return timeUnit.convert(abs(millis - other.millis), TimeUnit.MILLISECONDS)
  }
  
  companion object {
    
    fun current(): DateTime {
      return DateTime(Date().time)
    }
  
    fun ofMillis(millis: Long) = DateTime(millis)
    
    fun ofPattern(pattern: String, value: String): DateTime {
      val formatter = SimpleDateFormat(pattern, Locale.US)
      return DateTime(formatter.parse(value)!!.time)
    }
  }
}