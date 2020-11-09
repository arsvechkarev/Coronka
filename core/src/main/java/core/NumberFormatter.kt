package core

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object NumberFormatter {
  
  private val decimalFormatter: NumberFormat = DecimalFormat("#0.000")
  
  // Use FRANCE locale because it uses spaces for grouping digits (e.g 12354 -> 12 354)
  private val numberFormatter = NumberFormat.getInstance(Locale.FRANCE)
      .apply { isGroupingUsed = true }
  
  fun formatPercent(number: Number): String {
    return "${decimalFormatter.format(number)}%"
  }
  
  fun formatNumber(number: Number): String {
    return numberFormatter.format(number)
  }
}