package coreimpl

import core.DateTime
import core.DateTimeCreator
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

object ThreeTenAbpDateTimeCreator : DateTimeCreator {
  
  override fun createFromString(string: String): DateTime {
    val text = string.dropAfterLast(":")
    val dateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return ThreeTenAbpDateTime(dateTime)
  }
  
  override fun getCurrent(): DateTime {
    return ThreeTenAbpDateTime(LocalDateTime.now())
  }
  
  private fun String.dropAfterLast(string: String): String {
    val i = lastIndexOf(string)
    val lengthLeft = length - i
    return dropLast(lengthLeft)
  }
}