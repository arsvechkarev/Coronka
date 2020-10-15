package core.extenstions

import kotlin.random.Random

fun randomFloat(from: Float, to: Float): Float {
  return Random.nextInt(from.toInt(), to.toInt()).toFloat()
}

val Int.f get() = toFloat()
val Float.i get() = toInt()