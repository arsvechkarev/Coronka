package core

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object Application {
  
  val decimalFormatter: NumberFormat = DecimalFormat("#0.000")
  
  // Use FRANCE locale because it uses spaces for grouping digits (e.g 12354 -> 12 354)
  val numberFormatter: NumberFormat = NumberFormat.getInstance(Locale.FRANCE)
      .apply { isGroupingUsed = true }
}