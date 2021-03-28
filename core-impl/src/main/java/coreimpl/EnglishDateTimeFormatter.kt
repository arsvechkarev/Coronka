package coreimpl

import android.content.Context
import com.arsvechkarev.core.R
import com.arsvechkarev.core.R.string.text_date_time_just_now
import com.arsvechkarev.core.R.string.text_date_time_n_days_ago
import com.arsvechkarev.core.R.string.text_date_time_n_hours_ago
import com.arsvechkarev.core.R.string.text_date_time_n_minutes_ago
import com.arsvechkarev.core.R.string.text_date_time_n_weeks_ago
import com.arsvechkarev.core.R.string.text_date_time_one_hour_ago
import com.arsvechkarev.core.R.string.text_date_time_one_minute_ago
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
      applicationContext.getString(R.string.text_africa)
      when {
        daysDiff == 1 -> string(text_date_time_n_hours_ago, now.hour + (24 - date.hour))
        daysDiff < 14 -> string(text_date_time_n_days_ago, daysDiff)
        daysDiff < 21 -> string(text_date_time_n_weeks_ago, 2)
        daysDiff < 28 -> string(text_date_time_n_weeks_ago, 3)
        daysDiff < 365 -> {
          val monthName = applicationContext.getString(getMonthNameStringId(date.monthValue))
          "$monthName ${date.dayOfMonth}"
        }
        else -> "${date.monthValue}/${date.dayOfMonth}/${date.year}"
      }
    } else {
      when {
        date.hour < now.hour -> {
          // Date is more that one hour before now
          val diff = now.hour - date.hour
          if (diff == 1) {
            string(text_date_time_one_hour_ago)
          } else {
            string(text_date_time_n_hours_ago, diff)
          }
        }
        date.minute < now.minute -> {
          val diff = now.minute - date.minute
          if (diff == 1) {
            string(text_date_time_one_minute_ago)
          } else {
            string(text_date_time_n_minutes_ago, diff)
          }
        }
        date.minute == now.minute -> string(text_date_time_just_now)
        else -> throw IllegalStateException("Unable to parse: $date")
      }
    }
  }
  
  private fun string(id: Int, vararg args: Any): String {
    return applicationContext.getString(id, *args)
  }
  
  private fun getMonthNameStringId(monthValue: Int) = when (monthValue) {
    1 -> R.string.month_january
    2 -> R.string.month_february
    3 -> R.string.month_march
    4 -> R.string.month_april
    5 -> R.string.month_may
    6 -> R.string.month_june
    7 -> R.string.month_july
    8 -> R.string.month_august
    9 -> R.string.month_september
    10 -> R.string.month_october
    11 -> R.string.month_november
    12 -> R.string.month_december
    else -> throw IllegalStateException("Unknown month value: $monthValue")
  }
}