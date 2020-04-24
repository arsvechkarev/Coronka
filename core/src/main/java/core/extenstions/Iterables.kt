package core.extenstions

import androidx.collection.SparseArrayCompat

inline fun <T> SparseArrayCompat<T>.forEach(block: (T) -> Unit) {
  for (i in 0 until this.size()) {
    block(this[keyAt(i)]!!)
  }
}