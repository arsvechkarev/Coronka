package core.navigation

import androidx.lifecycle.LifecycleObserver
import core.BaseFragment
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  val currentFragment: BaseFragment?
  
  fun navigateTo(fragmentClass: KClass<out BaseFragment>)
  
  fun handleOnDrawerItemClicked(id: Int)
}