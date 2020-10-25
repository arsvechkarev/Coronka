package core.navigation

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import core.BaseFragment
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  val currentFragment: BaseFragment?
  
  fun switchTo(fragmentClass: KClass<out BaseFragment>)
  
  fun navigateTo(fragmentClass: KClass<out BaseFragment>, data: Bundle? = null)
  
  fun handleOnDrawerItemClicked(tag: String)
}