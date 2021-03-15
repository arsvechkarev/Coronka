package core.extenstions

import com.arsvechkarev.core.BuildConfig
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random

@OptIn(ExperimentalContracts::class)
inline fun assertThat(condition: Boolean, lazyMessage: () -> String = { "" }) {
  contract {
    callsInPlace(lazyMessage, InvocationKind.EXACTLY_ONCE)
    returns() implies condition
  }
  if (BuildConfig.DEBUG) {
    if (!condition) {
      throw AssertionError(lazyMessage())
    }
  }
}

val Int.f get() = toFloat()
val Float.i get() = toInt()

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