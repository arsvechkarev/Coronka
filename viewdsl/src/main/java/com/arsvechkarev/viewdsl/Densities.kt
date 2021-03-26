@file:Suppress("ObjectPropertyName")

package com.arsvechkarev.viewdsl

import android.content.res.Resources

object Densities {
  
  private var _density: Float = 1f
  
  val density: Float get() = _density
  
  fun init(resources: Resources) {
    _density = resources.displayMetrics.density
  }
}