package core.extenstions

import android.content.Context
import com.arsvechkarev.core.R

fun String.dropAfterLast(string: String): String {
  val i = lastIndexOf(string)
  val lengthLeft = length - i
  return dropLast(lengthLeft)
}

fun Number.toFormattedShortString(context: Context): String {
  val number = this.toInt()
  return when {
    number > 1_000_000 -> context.getString(R.string.number_millions, number / 1_000_000)
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