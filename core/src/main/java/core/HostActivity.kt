package core

import androidx.fragment.app.Fragment

interface HostActivity {
  
  fun openDrawer()
  
  fun enableTouchesOnDrawer()
  
  fun disableTouchesOnDrawer()
  
  interface DrawerOpenCloseListener {
    
    fun onDrawerOpened() {}
    
    fun onDrawerClosed() {}
  }
}

val Fragment.hostActivity: HostActivity
  get() = requireActivity() as HostActivity
