package core

/**
 * Represents network connection state
 */
interface NetworkConnection {
  
  /** Returns true if there is an available network */
  val isConnected: Boolean
  
  /** Clean up resources if needed */
  fun release() {}
  
  val isNotConnected get() = !isConnected
}