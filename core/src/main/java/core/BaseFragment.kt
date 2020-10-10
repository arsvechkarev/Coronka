package core

import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutResId: Int = 0) : Fragment(layoutResId) {
  
  val drawerOpenCloseListener = object : HostActivity.DrawerOpenCloseListener {
    override fun onDrawerClosed() = this@BaseFragment.onDrawerClosed()
    override fun onDrawerOpened() = this@BaseFragment.onDrawerOpened()
  }
  
  open fun onAppearedOnScreen() = Unit
  
  open fun onNetworkAvailable() = Unit
  
  open fun onDrawerOpened() = Unit
  
  open fun onDrawerClosed() = Unit
}