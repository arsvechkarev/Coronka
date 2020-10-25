package com.arsvechkarev.coronka

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
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
import core.navigation.Navigator
import kotlin.reflect.KClass

class MainNavigator(
  private var supportFragmentManager: FragmentManager?,
  private var drawerLayout: DrawerLayout?
) : Navigator {
  
  private val fragments = HashMap<KClass<*>, BaseFragment>()
  
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
        transaction.add(R.id.fragmentContainer, fragment)
      } else {
        transaction.show(fragment)
      }
    } else {
      transaction.replace(R.id.fragmentContainer, fragment)
    }
    drawerLayout.addOpenCloseListener(fragment.drawerOpenCloseListener)
    transaction.runOnCommit { fragment.onAppearedOnScreen() }
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
  
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    drawerLayout = null
    supportFragmentManager = null
  }
}