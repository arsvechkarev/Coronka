package com.arsvechkarev.map.utils

import com.google.android.gms.maps.GoogleMap
import java.util.concurrent.CountDownLatch

class MapHolder {
  
  private var googleMap: GoogleMap? = null
  private val initLatch = CountDownLatch(1)
  private var actions = ArrayList<(GoogleMap) -> Unit>()
  
  fun init(map: GoogleMap) {
    googleMap = map
    initLatch.countDown()
  }
  
  fun execute(action: GoogleMap.() -> Unit) {
    if (googleMap != null) {
      action(googleMap!!)
      return
    }
    actions.add(action)
    initLatch.await()
    actions.forEach { it(googleMap!!) }
    actions.clear()
  }
}