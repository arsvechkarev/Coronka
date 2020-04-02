package core

import android.content.Context
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker
import java.text.NumberFormat
import java.util.Locale

interface Application {
  
  object Threader {
    val backgroundWorker: Worker = BackgroundWorker.default()
    val ioWorker: Worker = BackgroundWorker.io()
    val mainThreadWorker: Worker = MainThreadWorker()
  }
  
  object Values {
    var density: Float = -1f
    var scaledDensity: Float = -1f
  }
  
  object Singletons {
    lateinit var applicationContext: Context
      private set
    
    val numberFormatter: NumberFormat = NumberFormat.getInstance(Locale.US).apply {
      isGroupingUsed = true
    }
    
    fun init(context: Context) {
      applicationContext = context
    }
  }
}