package core.state

import androidx.lifecycle.MutableLiveData

class StateHandle<T : BaseScreenState> {
  
  var currentValue: T? = null
    internal set
  
  internal var allowUpdate = false
  
  fun update(value: T) {
    currentValue = value
  }
  
  fun handleUpdate(block: (T) -> Unit) {
    if (allowUpdate) {
      block(currentValue!!)
    }
  }
}

val <T : BaseScreenState> MutableLiveData<StateHandle<T>>.currentValue get() = value!!.currentValue!!

fun <T : BaseScreenState, S : T> MutableLiveData<StateHandle<T>>.update(value: S) {
  this.value!!.update(value)
  updateSelf(isRecreated = false)
}

fun <T : BaseScreenState> MutableLiveData<StateHandle<T>>.updateSelf(isRecreated: Boolean) {
  value!!.allowUpdate = true
  value!!.currentValue!!.isStateRecreated = isRecreated
  value = value
  value!!.allowUpdate = false
}