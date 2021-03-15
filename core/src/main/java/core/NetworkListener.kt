package core

/**
 * Listens for network availability
 *
 * @see NetworkAvailabilityNotifier
 */
interface NetworkListener {
  
  fun onNetworkAvailable()
}