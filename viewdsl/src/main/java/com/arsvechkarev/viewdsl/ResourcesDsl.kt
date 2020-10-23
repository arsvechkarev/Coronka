@file:Suppress("ObjectPropertyName")

package com.arsvechkarev.viewdsl

object Floats {
  val Int.dp: Float get() = Densities.density * this
}

object Ints {
  val Int.dp: Int get() = (Densities.density * this).toInt()
}