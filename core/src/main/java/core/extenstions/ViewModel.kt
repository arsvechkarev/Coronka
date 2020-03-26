package core.extenstions

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.updateSelf() {
  value = value
}