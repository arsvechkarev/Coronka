package core

import androidx.fragment.app.Fragment

interface HostActivity {
  
  fun onDrawerIconClicked()
  
  fun enableDrawer()
  
  fun disableDrawer()
  
  fun addDrawerOpenCloseListener(listener: DrawerOpenCloseListener)
  
  fun removeDrawerOpenCloseListener(listener: DrawerOpenCloseListener)
  
  interface DrawerOpenCloseListener {
    
    fun onDrawerOpened() {}
    
    fun onDrawerClosed() {}
  }
}

val Fragment.hostActivity: HostActivity
  get() = requireActivity() as HostActivity
