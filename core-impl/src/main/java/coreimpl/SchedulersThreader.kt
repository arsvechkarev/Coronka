package coreimpl

import api.threading.Threader
import core.Schedulers

class SchedulersThreader(
  private val schedulers: Schedulers
) : Threader {
  
  override fun onMainThread(action: () -> Unit) {
    schedulers.mainThread().scheduleDirect(action)
  }
  
  override fun onBackgroundThread(action: () -> Unit) {
    schedulers.computation().scheduleDirect(action)
  }
  
  override fun onIoThread(action: () -> Unit) {
    schedulers.io().scheduleDirect(action)
  }
}