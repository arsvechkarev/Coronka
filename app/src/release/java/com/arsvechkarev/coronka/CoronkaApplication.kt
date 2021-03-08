package com.arsvechkarev.coronka

import android.util.Log
import timber.log.Timber
import com.arsvechkarev.common.CoreDiComponent

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(ReleaseTree)
    CoreDiComponent.initDefault(applicationContext)
  }
  
  object ReleaseTree : Timber.Tree() {
    
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
      if (t != null) {
        Log.println(priority, tag, message + "\n" + Log.getStackTraceString(t))
      }
    }
  }
}