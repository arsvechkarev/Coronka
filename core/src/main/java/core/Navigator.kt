package core

import androidx.lifecycle.LifecycleObserver
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  fun switchTo(fragmentClass: KClass<out BaseFragment>)
  
  fun handleOnDrawerItemClicked(tag: String)
  
  fun allowBackPress(): Boolean
}