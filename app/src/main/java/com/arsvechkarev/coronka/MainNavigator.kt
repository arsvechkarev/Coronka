package com.arsvechkarev.coronka

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextMap
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextNews
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextRankings
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextStatistics
import com.arsvechkarev.coronka.presentation.MainActivity.Companion.TextTips
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import com.arsvechkarev.views.DrawerLayout
import core.BaseFragment
import core.Navigator
import kotlin.reflect.KClass

class MainNavigator(
  private var supportFragmentManager: FragmentManager?,
  private var drawerLayout: DrawerLayout?,
  private var onFragmentAppeared: (Fragment) -> Unit
) : Navigator, LifecycleObserver {
  
  private val fragments = HashMap<KClass<out BaseFragment>, BaseFragment>()
  private val stack = ArrayList<BaseFragment>()
  
  override var currentFragment: BaseFragment? = null
  
  override fun switchTo(fragmentClass: KClass<out BaseFragment>) {
    val supportFragmentManager = supportFragmentManager ?: return
    val drawerLayout = drawerLayout ?: return
    if (currentFragment?.javaClass?.name == fragmentClass.java.name) {
      return
    }
    val fragment = fragments[fragmentClass] ?: fragmentClass.java.newInstance()
    fragments[fragmentClass] = fragment
    val transaction = supportFragmentManager.beginTransaction()
    if (currentFragment != null) {
      drawerLayout.removeOpenCloseListener(currentFragment!!.drawerOpenCloseListener)
      transaction.hide(currentFragment!!)
      if (!fragment.isAdded) {
        stack.add(fragment)
        transaction.add(R.id.fragmentContainer, fragment)
      } else {
        if (stack.contains(fragment)) {
          stack.swap(stack.indexOf(fragment), stack.indexOf(currentFragment!!))
        } else {
          stack.add(fragment)
        }
        transaction.show(fragment)
      }
    } else {
      stack.add(fragment)
      transaction.replace(R.id.fragmentContainer, fragment)
    }
    drawerLayout.addOpenCloseListener(fragment.drawerOpenCloseListener)
    transaction.runOnCommit {
      drawerLayout.respondToTouches = true
      fragment.onAppearedOnScreen()
    }
    transaction.commit()
    currentFragment = fragment
  }
  
  override fun navigateTo(fragmentClass: KClass<out BaseFragment>, data: Bundle?) {
    val fragmentManager = supportFragmentManager ?: return
    val fragment = fragmentClass.java.newInstance()
    fragment.arguments = data
    fragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .commit()
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
      val last = stack.removeLast()
      val fragmentManager = supportFragmentManager ?: return true
      val newLast = stack.last()
      currentFragment = newLast
      onFragmentAppeared(newLast)
      fragmentManager.beginTransaction()
          .show(newLast)
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
  }
  
  private fun <T> ArrayList<T>.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
  }
}