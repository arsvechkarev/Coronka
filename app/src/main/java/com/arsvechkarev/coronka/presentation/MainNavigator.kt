package com.arsvechkarev.coronka.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import base.BaseFragment
import base.Navigator
import base.views.DrawerLayout
import com.arsvechkarev.coronka.R
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import kotlin.reflect.KClass

class MainNavigator(
  private var supportFragmentManager: FragmentManager?,
  private var drawerLayout: DrawerLayout?,
  private var onFragmentAppeared: (Fragment) -> Unit
) : Navigator, LifecycleObserver {
  
  private val stack = ArrayList<String>() // Stack of current fragment names
  
  private var currentFragment: BaseFragment? = null
  
  override fun switchTo(fragmentClass: KClass<out BaseFragment>) {
    val fragmentManager = supportFragmentManager ?: return
    val drawerLayout = drawerLayout ?: return
    if (currentFragment?.javaClass?.name == fragmentClass.java.name) {
      return
    }
    val tag = fragmentClass.java.name
    val fragment = fragmentManager.findFragmentByTag(tag) ?: fragmentClass.java.newInstance()
    require(fragment is BaseFragment)
    val transaction = fragmentManager.beginTransaction()
    val fragmentName = fragment.getNameForStack()
    if (currentFragment != null) {
      drawerLayout.removeOpenCloseListener(currentFragment!!.drawerOpenCloseListener)
      transaction.hide(currentFragment!!)
      if (!fragment.isAdded) {
        stack.add(fragmentName)
        transaction.add(R.id.fragmentContainer, fragment, fragmentName)
      } else {
        if (stack.contains(fragmentName)) {
          stack.swap(fragmentName, currentFragment!!.getNameForStack())
        } else {
          stack.add(fragmentName)
        }
        transaction.show(fragment)
      }
    } else {
      stack.add(fragmentName)
      transaction.add(R.id.fragmentContainer, fragment, fragmentName)
    }
    drawerLayout.addOpenCloseListener(fragment.drawerOpenCloseListener)
    transaction.runOnCommit {
      if (fragment.enableTouchesOnDrawerWhenFragmentAppears) {
        drawerLayout.respondToTouches = true
      }
    }
    transaction.commit()
    currentFragment = fragment
  }
  
  override fun handleOnDrawerItemClicked(tag: String) {
    drawerLayout?.close(andThen = {
      when (tag) {
        TextStatistics -> switchTo(StatsFragment::class)
        TextNews -> switchTo(NewsFragment::class)
        TextMap -> switchTo(MapFragment::class)
        TextRankings -> switchTo(RankingsFragment::class)
        TextTips -> switchTo(TipsFragment::class)
      }
    })
  }
  
  override fun allowBackPress(): Boolean {
    if (stack.isEmpty()) return true
    if (currentFragment?.allowBackPress() == true) {
      if (stack.size == 1) return true
      val fragmentManager = supportFragmentManager ?: return true
      val last = fragmentManager.getFragmentByName(stack.removeLast()) ?: return true
      val newFragment = fragmentManager.getFragmentByName(stack.last()) ?: return true
      currentFragment = newFragment
      onFragmentAppeared(newFragment)
      fragmentManager.beginTransaction()
          .show(newFragment)
          .hide(last)
          .commit()
    }
    return false
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    drawerLayout = null
    supportFragmentManager = null
    onFragmentAppeared = {}
    currentFragment = null
  }
  
  private fun Fragment.getNameForStack() = this::class.java.name
  
  private fun FragmentManager.getFragmentByName(fragmentClassName: String): BaseFragment? {
    return fragments.find { it.getNameForStack() == fragmentClassName } as? BaseFragment
  }
  
  private fun <T> ArrayList<T>.swap(e1: T, e2: T) {
    val i = indexOf(e1)
    val j = indexOf(e2)
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
  }
}