package core

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import kotlin.reflect.KClass

interface Navigator : LifecycleObserver {
  
  fun switchTo(fragmentClass: KClass<out BaseFragment>)
  
  fun navigateTo(fragmentClass: KClass<out BaseFragment>, data: Bundle? = null)
  
  fun handleOnDrawerItemClicked(tag: String)
  
  fun allowBackPress(): Boolean
}