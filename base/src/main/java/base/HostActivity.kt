package base

import androidx.fragment.app.Fragment

/**
 * Host activity with necessary methods to be accessed from fragment
 */
interface HostActivity {
  
  /** Opens navigation drawer */
  fun openDrawer()
  
  /** Enables drawer reaction to touches */
  fun enableTouchesOnDrawer()
  
  /** Disables drawer reaction to touches */
  fun disableTouchesOnDrawer()
  
  /** Listener for observing drawer events */
  interface DrawerOpenCloseListener {
    
    /** Called when drawer fully opened */
    fun onDrawerOpened() {}
    
    /** Called when drawer fully closed */
    fun onDrawerClosed() {}
  }
}

val Fragment.hostActivity: HostActivity
  get() = requireActivity() as HostActivity
