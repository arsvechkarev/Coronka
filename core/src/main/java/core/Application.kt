package core

import android.content.Context
import android.content.res.Resources
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object Application {
  
  lateinit var applicationContext: Context
    private set
  
  var density: Float = 1f
  
  var scaledDensity: Float = 1f
  
  val decimalFormatter: NumberFormat = DecimalFormat("#0.000")
  
  // Use FRANCE locale because it uses spaces for grouping digits (e.g 12354 -> 12 354)
  val numberFormatter: NumberFormat = NumberFormat.getInstance(Locale.FRANCE)
      .apply {
        isGroupingUsed = true
      }
  
  fun init(context: Context) {
    applicationContext = context
  }
  
  fun initDensities(resources: Resources) {
    density = resources.displayMetrics.density
    scaledDensity = resources.displayMetrics.scaledDensity
  }
}