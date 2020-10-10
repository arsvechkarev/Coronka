package core.extenstions

import androidx.collection.SparseArrayCompat

inline fun <T> SparseArrayCompat<T>.forEach(block: (T) -> Unit) {
  for (i in 0 until this.size()) {
    block(this[keyAt(i)]!!)
  }
}

inline fun <K, V> Map<K, V>.iterate(action: (key: K, value: V) -> Unit) {
  for ((key, value) in entries) {
    action(key, value)
  }
}