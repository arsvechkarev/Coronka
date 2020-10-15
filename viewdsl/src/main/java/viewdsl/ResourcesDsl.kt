@file:Suppress("ObjectPropertyName")

package viewdsl

object Floats {
  val Int.dp: Float get() = Densities.density * this
}

object Ints {
  val Int.dp: Int get() = (Densities.density * this).toInt()
}

val Int.f get() = toFloat()
val Float.i get() = toInt()