package core

/**
 * Represents a state whether drawer [isOpened] or not
 */
data class DrawerState(val isOpened: Boolean) {
  
  val isClosed get() = !isOpened
}