package coreimpl

import android.content.Context
import core.DateTimeCreator
import core.DateTimeFormatter

class EnglishDateTimeFormatter(
  private val applicationContext: Context,
  private val dateTimeCreator: DateTimeCreator
) : DateTimeFormatter {
  
  override fun formatPublishedDate(stringDate: String): String {
    val date = dateTimeCreator.createFromString(stringDate)
    val now = dateTimeCreator.getCurrent()
    val daysDiff = (now.year - date.year) * 366 - date.dayOfYear + now.dayOfYear
    return if (daysDiff > 0) {
      when {
        daysDiff == 1 -> "${now.hour + (24 - date.hour)} hours ago"
        daysDiff < 14 -> "$daysDiff days ago"
        daysDiff < 21 -> "2 weeks ago"
        daysDiff < 28 -> "3 weeks ago"
        daysDiff < 365 -> "${date.monthName} ${date.dayOfMonth}"
        else -> "${date.monthValue}/${date.dayOfMonth}/${date.year}"
      }
    } else {
      when {
        date.hour < now.hour -> {
          // Date is more that one hour before now
          val diff = now.hour - date.hour
          if (diff == 1) "1 hour ago" else "$diff hours ago"
        }
        date.minute < now.minute -> {
          val diff = now.minute - date.minute
          if (diff == 1) "1 minute ago" else "$diff minutes ago"
        }
        date.minute == now.minute -> "Just now"
        else -> throw IllegalStateException("Unable to parse: $date")
      }
    }
  }
}