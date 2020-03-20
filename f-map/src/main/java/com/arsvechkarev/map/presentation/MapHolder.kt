package com.arsvechkarev.map.presentation

import com.google.android.gms.maps.GoogleMap
import core.ApplicationConfig
import java.util.concurrent.CountDownLatch

class MapHolder(private val threader: ApplicationConfig.Threader) {
  
  private var googleMap: GoogleMap? = null
  private val initLatch = CountDownLatch(1)
  
  fun init(map: GoogleMap) {
    initLatch.countDown()
    googleMap = map
  }
  
  fun addAction(action: (GoogleMap) -> Unit) {
    if (googleMap != null) {
      action(googleMap!!)
      return
    }
    threader.backgroundWorker.submit {
      initLatch.await()
      threader.mainThreadWorker.submit {
        action(googleMap!!)
      }
    }
  }
  
}