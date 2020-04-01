package core

import kotlin.reflect.KClass

class SavedData {
  
  @PublishedApi
  internal val map = HashMap<KClass<out Any>, Any>()
  
  inline fun <reified T : Any> doIfHas(action: (T) -> Unit) {
    if (map.containsValue(T::class)) {
      action(map[T::class] as T)
    }
  }
  
  inline fun <reified T : Any> add(value: T) {
    map[T::class] = value
  }
  
  @Suppress("UNCHECKED_CAST")
  inline fun <reified T : Any> get(): T {
    if (map.containsValue(T::class)) {
      throw IllegalStateException("No value for class: ${T::class}")
    }
    return map[T::class] as T
  }
}