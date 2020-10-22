package com.arsvechkarev.coronka

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
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
  
  override fun navigateTo(fragmentClass: KClass<out BaseFragment>) {
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
        transaction.add(R.id.fragment_container, fragment)
      } else {
        transaction.show(fragment)
      }
    } else {
      transaction.replace(R.id.fragment_container, fragment)
    }
    drawerLayout.addOpenCloseListener(fragment.drawerOpenCloseListener)
    transaction.runOnCommit { fragment.onAppearedOnScreen() }
    transaction.commit()
    currentFragment = fragment
  }
  
  override fun handleOnDrawerItemClicked(id: Int) {
    drawerLayout?.close(andThen = {
      when (id) {
        R.id.drawerTextStatistics -> navigateTo(StatsFragment::class)
        R.id.drawerTextNews -> navigateTo(NewsFragment::class)
        R.id.drawerTextMap -> navigateTo(MapFragment::class)
        R.id.drawerTextTips -> navigateTo(TipsFragment::class)
        R.id.drawerTextRankings -> navigateTo(RankingsFragment::class)
      }
    })
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    drawerLayout = null
    supportFragmentManager = null
  }
}