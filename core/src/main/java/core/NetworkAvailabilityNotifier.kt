package core

/**
 * Registers and unregisters network listeners
 *
 * @see AndroidNetworkAvailabilityNotifier
 */
interface NetworkAvailabilityNotifier {
  
  fun registerListener(listener: NetworkListener)
  
  fun unregisterListener(listener: NetworkListener)
}