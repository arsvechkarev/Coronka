package com.arsvechkarev.coronavirusinfo

import kotlin.math.log10

fun main() {
  val x = 0.3f
  println(x.normalize())
}

private fun Float.normalize(): Float {
  return (log10(this * 0.2 + 0.03) + 1.65).toFloat()
}
