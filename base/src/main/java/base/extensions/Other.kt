package base.extensions

import kotlin.random.Random

fun <T> T.ifTrue(condition: (T) -> Boolean, action: T.() -> Unit) {
  if (condition(this)) action(this)
}

inline val Int.f get() = toFloat()
inline val Float.i get() = toInt()

inline fun <K, V> Map<K, V>.iterate(action: (key: K, value: V) -> Unit) {
  for ((key, value) in entries) {
    action(key, value)
  }
}

fun randomFloat(from: Float, to: Float): Float {
  return Random.nextInt(from.toInt(), to.toInt()).toFloat()
}

fun String.dropAfterLast(string: String): String {
  val i = lastIndexOf(string)
  val lengthLeft = length - i
  return dropLast(lengthLeft)
}