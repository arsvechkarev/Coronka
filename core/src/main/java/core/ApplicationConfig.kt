package core

import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker

interface ApplicationConfig {
  
  object Threader {
    val backgroundWorker: Worker = BackgroundWorker.default()
    val ioWorker: Worker = BackgroundWorker.io()
    val mainThreadWorker: Worker = MainThreadWorker()
  }
  
  object Densities {
    var density: Float = -1f
    var scaledDensity: Float = -1f
  }
}