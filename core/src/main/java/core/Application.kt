package core

import android.content.Context
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

interface Application {
  
  object Values {
    var density: Float = -1f
    var scaledDensity: Float = -1f
  }
  
  object Singletons {
    lateinit var applicationContext: Context
      private set
    
    val decimalFormatter: NumberFormat = DecimalFormat("#0.000")
    
    val numberFormatter: NumberFormat = NumberFormat.getInstance(Locale.US).apply {
      isGroupingUsed = true
    }
    
    fun init(context: Context) {
      applicationContext = context
    }
  }
}