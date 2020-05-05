package core.releasable

/**
 * Represents a resource that should be released
 */
interface Releasable {
  
  fun release()
}