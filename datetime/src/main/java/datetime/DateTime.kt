package datetime

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DateTime(
  private val millis: Long
) {
  
  fun differenceWith(other: DateTime, timeUnit: TimeUnit): Long {
    return timeUnit.convert(abs(millis - other.millis), TimeUnit.MILLISECONDS)
  }
  
  override fun toString() = millis.toString()
  
  companion object {
    
    fun current(): DateTime {
      return DateTime(Date().time)
    }
    
    fun ofString(millis: String) = DateTime(millis.toLong())
    
    fun ofPattern(pattern: String, value: String): DateTime {
      val formatter = SimpleDateFormat(pattern, Locale.US)
      return DateTime(formatter.parse(value)!!.time)
    }
  }
}