package core.extenstions

import android.content.Context
import com.arsvechkarev.core.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

private val decimalFormatter: NumberFormat = DecimalFormat("#0.000")

// Use FRANCE locale because it uses spaces for grouping digits (e.g 12354 -> 12 354)
private val numberFormatter = NumberFormat.getInstance(Locale.FRANCE)
    .apply { isGroupingUsed = true }


fun Number.formatGeneralInfo(context: Context): String {
  return formattedMillions(context)
}

fun String.toFormattedGraphDate(): String {
  return toFormattedDate(TextStyle.SHORT)
}

fun String.toFormattedTextLabelDate(): String {
  return toFormattedDate(TextStyle.FULL)
}

fun Number.toFormattedNumber(): String {
  return numberFormatter.format(this)
}

fun Number.toFormattedDecimalNumber(): String {
  return decimalFormatter.format(this)
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

fun Int.formatRankingsNumber(): String {
  return "$this."
}

private fun Number.formattedMillions(context: Context): String {
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

private fun String.toFormattedDate(monthNameStyle: TextStyle): String {
  val localDate = LocalDate.parse(this)
  val monthName = localDate.month.getDisplayName(monthNameStyle, Locale.US)
  return "$monthName ${localDate.dayOfMonth}"
}
