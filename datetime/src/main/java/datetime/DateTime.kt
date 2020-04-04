package datetime

import datetime.DateManager.calendar
import datetime.DateManager.mutableCalendar
import java.text.SimpleDateFormat
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.HOUR
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Locale

class DateTime(
  private val year: Int,
  private val month: Int,
  private val day: Int,
  private val hour: Int,
  private val minute: Int
) {
  
  fun formatted(pattern: String): String {
    mutableCalendar.set(YEAR, year)
    mutableCalendar.set(MONTH, month)
    mutableCalendar.set(DAY_OF_MONTH, day)
    mutableCalendar.set(HOUR, hour)
    mutableCalendar.set(MINUTE, minute)
    return SimpleDateFormat(pattern, Locale.US).format(mutableCalendar.time)
  }
  
  fun string(): String {
    return "$year$DIVIDER$month$DIVIDER$day$DIVIDER$hour$DIVIDER$minute"
  }
  
  override fun toString() = string()
  
  companion object {
    
    private const val DIVIDER = "-"
    
    fun current() = DateTime(
      calendar[YEAR],
      calendar[MONTH],
      calendar[DAY_OF_MONTH],
      calendar[HOUR],
      calendar[MINUTE]
    )
    
    fun ofString(string: String): DateTime {
      val units = string.split(DIVIDER)
      val year = units[0].toInt()
      val month = units[1].toInt()
      val day = units[2].toInt()
      val hour = units[3].toInt()
      val minute = units[4].toInt()
      return DateTime(year, month, day, hour, minute)
    }
  }
}