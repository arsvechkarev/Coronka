package core

/**
 * Registers and unregisters network listeners
 */
interface NetworkAvailabilityNotifier {
  
  fun registerListener(listener: NetworkListener)
  
  fun unregisterListener(listener: NetworkListener)
}