package core

/**
 * Represents network connection state
 */
interface NetworkConnection {
  
  /** Returns true if there is an available network */
  val isConnected: Boolean
  
  val isNotConnected get() = !isConnected
}