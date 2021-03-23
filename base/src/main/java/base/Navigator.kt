package base

import androidx.lifecycle.LifecycleObserver
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  fun switchTo(fragmentClass: KClass<out base.BaseFragment>)
  
  fun handleOnDrawerItemClicked(tag: String)
  
  fun allowBackPress(): Boolean
}