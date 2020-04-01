package core

import kotlin.reflect.KClass

class SavedData {
  
  private val map = HashMap<KClass<out Any>, Any>()
  
  fun <T : Any> doIfHas(klass: KClass<T>, action: (T) -> Unit) {
    if (map.containsValue(klass)) {
      action(map[klass] as T)
    }
  }
  
  fun <T : Any> add(value: T) {
    map[value::class] = value
  }
}