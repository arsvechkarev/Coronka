package com.arsvechkarev.map.presentation

import com.google.android.gms.maps.GoogleMap
import core.concurrency.AndroidThreader
import core.concurrency.Threader
import java.util.concurrent.CountDownLatch

class MapHolder(private val threader: Threader = AndroidThreader) {
  
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
    threader.onBackground {
      initLatch.await()
      threader.onMainThread {
        action(googleMap!!)
      }
    }
  }
}