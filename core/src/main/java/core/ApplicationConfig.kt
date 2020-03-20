package core

import core.async.BackgroundWorker
import core.async.Worker

object ApplicationConfig {
  
  val backgroundWorker: Worker = BackgroundWorker.default()
}