package core

import core.async.BackgroundWorker
import core.async.Worker
import java.util.concurrent.Executors

object ApplicationConfig {
  
  val backgroundWorker: Worker = BackgroundWorker(Executors.newSingleThreadExecutor())
}