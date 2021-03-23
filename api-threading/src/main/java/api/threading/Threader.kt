package api.threading

/**
 * Executes code blocks on different threads
 */
interface Threader {
  
  /**
   * Executes [action] on main thread
   */
  fun onMainThread(action: () -> Unit)
  
  /**
   * Executes [action] on background thread
   */
  fun onBackgroundThread(action: () -> Unit)
  
  /**
   * Executes [action] on io thread
   */
  fun onIoThread(action: () -> Unit)
}