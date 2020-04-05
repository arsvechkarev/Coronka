package core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KClass

/**
 * Base class for dealing with multiple states in view model
 */
class StateHandle<S : Any> {
  
  private var value: Any? = null
  
  internal var newState: S? = null
  
  internal var allowHandle: Boolean = false
  
  val states = LinkedHashMap<KClass<out S>, S>()
  
  fun <T : S> update(state: T) {
    value = Any()
    remove(state::class)
    newState = state
    states[state::class] = state
  }
  
  fun <T : S> setOnly(state: T) {
    clear()
    newState = state
    states[state::class] = state
  }
  
  @Suppress("UNCHECKED_CAST")
  fun <T : S> get(stateClass: KClass<T>): T {
    return (states[stateClass] as T)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun <T : S> doIfContains(stateClass: KClass<T>, action: T.() -> Unit) {
    if (states.containsKey(stateClass)) action(states[stateClass] as T)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun <T : S> assertIfContains(stateClass: KClass<T>, action: T.() -> Unit) {
    action(states[stateClass] as T)
  }
  
  fun <T : S> remove(stateClass: KClass<T>) = states.remove(stateClass)
  
  fun <T : S> contains(stateClass: KClass<T>) = states.containsKey(stateClass)
  
  fun clear() {
    newState = null
    states.clear()
  }
  
  /**
   * Iterates over all states applying given action or applies action to a new state (if
   * such state is not null)
   *
   * @see updateAll
   */
  fun handleUpdate(action: (S) -> Unit) {
    if (!allowHandle) {
      return
    }
    if (newState != null) {
      action(newState!!)
    } else {
      states.values.forEach(action)
    }
  }
  
  fun size(): Int {
    return states.size
  }
}


/**
 * Adds value to states map (or updates if the value already exists). Also can optionally remove state by
 * specified [remove]
 */
fun <T : S, R : S, S : Any> MutableLiveData<StateHandle<S>>.update(state: T, remove: KClass<R>? = null) {
  value!!.update(state)
  if (remove != null) {
    value!!.remove(remove)
  }
  updateSelf()
}

fun <T : S, S : Any> LiveData<StateHandle<S>>.contains(stateClass: KClass<T>): Boolean {
  return value!!.contains(stateClass)
}

fun <T : S, S : Any> LiveData<StateHandle<S>>.remove(stateClass: KClass<T>) {
  value!!.remove(stateClass)
}

fun <T : S, S : Any> MutableLiveData<StateHandle<S>>.doIfContains(
  stateClass: KClass<T>,
  action: T.() -> Unit
) {
  value!!.doIfContains(stateClass, action)
  updateSelf()
}

fun <S : Any> MutableLiveData<StateHandle<S>>.updateAll() {
  value!!.newState = null
  updateSelf()
}

fun <T : Any> MutableLiveData<StateHandle<T>>.updateSelf() {
  value!!.allowHandle = true
  value = value
  value!!.allowHandle = false
}