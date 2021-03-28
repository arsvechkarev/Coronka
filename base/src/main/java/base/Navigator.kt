package base

import androidx.lifecycle.LifecycleObserver
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  fun switchTo(fragmentClass: KClass<out BaseFragment>)
  
  fun allowBackPress(): Boolean
}