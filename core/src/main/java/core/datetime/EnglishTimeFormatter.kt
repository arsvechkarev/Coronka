package core.datetime

class EnglishTimeFormatter : TimeFormatter {
  
  override fun formatPublishedDate(stringDate: String): String {
    val date = DateTime.of(stringDate)
    val now = DateTime.now()
    return if (date.dayOfYear != now.dayOfYear) {
      // If the date is not today, return full date
      "${date.year}.${date.monthValue}.${date.dayOfMonth}"
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