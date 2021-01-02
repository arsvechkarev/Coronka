package core.extenstions

import android.content.Context
import com.arsvechkarev.core.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.Locale

fun String.dropAfterLast(string: String): String {
  val i = lastIndexOf(string)
  val lengthLeft = length - i
  return dropLast(lengthLeft)
}

fun Number.toTotalCasesAmount(context: Context): String {
  val number = this.toInt()
  return when {
    number > 1_000_000 -> context.getString(R.string.number_millions, number / 1_000_000)
    number > 1000 -> context.getString(R.string.number_thousands, number / 1_000)
    else -> number.toString()
  }
}

fun Number.toNewCasesAmount(context: Context): String {
  val number = this.toInt()
  return when {
    number >= 1_000_000 -> {
      if (number / 1_000_000 == 0) {
        context.getString(R.string.number_millions, number / 1_000_000)
      } else {
        val remainingHundredsOfThousands = (number - number / 1_000_000) / 100_000
        context.getString(R.string.number_formatted_with_parts,
          number / 1_000_000, remainingHundredsOfThousands.toString())
      }
    }
    number > 1000 -> context.getString(R.string.number_thousands, number / 1_000)
    else -> number.toString()
  }
}

fun Number.formattedMillions(context: Context): String {
  val thousands = this.toInt() / 1000
  val millionsPart = thousands / 1000
  var thousandsPart = (thousands % 1000).toString()
  when {
    thousandsPart.isEmpty() -> thousandsPart = "000"
    thousandsPart.length == 1 -> thousandsPart = "00$thousandsPart"
    thousandsPart.length == 2 -> thousandsPart = "0$thousandsPart"
  }
  return context.getString(R.string.number_formatted_with_parts, millionsPart,
    thousandsPart)
}

fun String.toFormattedEnglishDate(monthNameStyle: TextStyle): String {
  val localDate = LocalDate.parse(this)
  val monthName = localDate.month.getDisplayName(monthNameStyle, Locale.US)
  return "$monthName ${localDate.dayOfMonth}"
}