package core.extenstions

fun Number.toFormattedShortString(): String {
  val number = this.toInt()
  return when {
    number > 1_000_000 -> "${number / 1_000_000} M"
    number > 1000 -> "${number / 1000} K"
    else -> number.toString()
  }
}

fun Number.formattedMillions(): String {
  val thousands = this.toInt() / 1000
  val millionsPart = thousands / 1000
  var thousandsPart = (thousands % 1000).toString()
  when {
    thousandsPart.isEmpty() -> thousandsPart = "000"
    thousandsPart.length == 1 -> thousandsPart = "00$thousandsPart"
    thousandsPart.length == 2 -> thousandsPart = "0$thousandsPart"
  }
  return "$millionsPart.$thousandsPart M"
}