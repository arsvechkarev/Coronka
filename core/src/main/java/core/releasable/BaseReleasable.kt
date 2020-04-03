package core.releasable

abstract class BaseReleasable : Releasable {
  
  private val objects = ArrayList<Any?>()
  
  fun addForRelease(vararg any: Any?) {
    objects.addAll(any)
  }
  
  override fun release() {
    objects.clear()
  }
}