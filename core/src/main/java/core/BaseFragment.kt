package core

import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutResId: Int = 0) : Fragment(layoutResId) {
  
  open fun onNetworkAvailable() = Unit
}