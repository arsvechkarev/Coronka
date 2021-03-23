package coreimpl

import core.DateTime
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.TextStyle
import java.util.Locale

class ThreeTenAbpDateTime(private val localDateTime: LocalDateTime) : DateTime {
  
  override val year = localDateTime.year
  override val monthName: String = localDateTime.month.getDisplayName(TextStyle.FULL, Locale.US)
  override val monthValue = localDateTime.monthValue
  override val dayOfMonth = localDateTime.dayOfMonth
  override val dayOfYear = localDateTime.dayOfYear
  override val hour = localDateTime.hour
  override val minute = localDateTime.minute
  
  override fun toString() = localDateTime.toString()
}