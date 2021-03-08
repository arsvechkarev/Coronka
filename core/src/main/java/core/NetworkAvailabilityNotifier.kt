package core

interface NetworkAvailabilityNotifier {
  
  fun registerListener(listener: NetworkListener)
  
  fun unregisterListener(listener: NetworkListener)
}