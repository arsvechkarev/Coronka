package core.releasable

import androidx.lifecycle.ViewModel

abstract class ReleasableViewModel(vararg releasables: Releasable) : ViewModel() {
  
  private val releasablesList = ArrayList<Releasable>(releasables.size).apply {
    addAll(releasables)
  }
  
  fun addReleasable(vararg releasable: Releasable) {
    releasablesList.addAll(releasable)
  }
  
  override fun onCleared() {
    releasablesList.forEach { it.release() }
    releasablesList.clear()
  }
}