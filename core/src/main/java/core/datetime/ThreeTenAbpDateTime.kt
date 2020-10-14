package core.datetime

import org.threeten.bp.LocalDateTime

class ThreeTenAbpDateTime(private val localDateTime: LocalDateTime) : DateTime {
  
  override val year = localDateTime.year
  override val monthValue = localDateTime.monthValue
  override val dayOfMonth = localDateTime.dayOfMonth
  override val dayOfYear = localDateTime.dayOfYear
  override val hour = localDateTime.hour
  override val minute = localDateTime.minute
  
  override fun toString() = localDateTime.toString()
}