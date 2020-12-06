package core.extenstions

inline fun <K, V> Map<K, V>.iterate(action: (key: K, value: V) -> Unit) {
  for ((key, value) in entries) {
    action(key, value)
  }
}