package core.datetime

import core.extenstions.dropAfterLast
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/** Represent a date and a time*/
interface DateTime {
  
  val year: Int
  val monthName: String
  val monthValue: Int
  val dayOfMonth: Int
  val dayOfYear: Int
  val hour: Int
  val minute: Int
  
  override fun toString(): String
  
  companion object {
    
    fun now(): DateTime {
      return ThreeTenAbpDateTime(LocalDateTime.now())
    }
    
    fun of(date: String): DateTime {
      val text = date.dropAfterLast(":")
      val dateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      return ThreeTenAbpDateTime(dateTime)
    }
  }
}