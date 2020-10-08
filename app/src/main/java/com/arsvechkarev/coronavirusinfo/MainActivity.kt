package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import core.Application
import core.HostActivity
import kotlinx.android.synthetic.main.activity_main.drawerGroupLinearLayout
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.drawerTextMap
import kotlinx.android.synthetic.main.activity_main.drawerTextRankings
import kotlinx.android.synthetic.main.activity_main.drawerTextStatistics
import kotlinx.android.synthetic.main.activity_main.drawerTextTips
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity(), HostActivity {
  
  private val fragments = HashMap<KClass<*>, Fragment>()
  private var currentFragment: Fragment? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.initDensities(resources)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    goToFragment(StatsFragment::class)
    drawerTextStatistics.isSelected = true
    val onDrawerItemClick: (v: View) -> Unit = { handleOnDrawerItemClicked(it) }
    drawerTextStatistics.setOnClickListener(onDrawerItemClick)
    drawerTextMap.setOnClickListener(onDrawerItemClick)
    drawerTextTips.setOnClickListener(onDrawerItemClick)
    drawerTextRankings.setOnClickListener(onDrawerItemClick)
  }
  
  override fun onDrawerIconClicked() {
    drawerLayout.open()
  }
  
  override fun enableDrawer() {
    drawerLayout.respondToTouches = true
  }
  
  override fun disableDrawer() {
    drawerLayout.respondToTouches = false
  }
  
  override fun addDrawerOpenCloseListener(listener: HostActivity.DrawerOpenCloseListener) {
    drawerLayout.addOpenCloseListener(listener)
  }
  
  override fun removeDrawerOpenCloseListener(listener: HostActivity.DrawerOpenCloseListener) {
    drawerLayout.removeOpenCloseListener(listener)
  }
  
  private fun handleOnDrawerItemClicked(view: View) {
    drawerGroupLinearLayout.onTextViewClicked(view)
    drawerLayout.close(andThen = {
      when (view) {
        drawerTextStatistics -> goToFragment(StatsFragment::class)
        drawerTextTips -> goToFragment(TipsFragment::class)
        drawerTextRankings -> goToFragment(RankingsFragment::class)
      }
    })
  }
  
  private fun goToFragment(fragmentClass: KClass<out Fragment>) {
    if (currentFragment?.javaClass?.name == fragmentClass.java.name) {
      return
    }
    val fragment = fragments[fragmentClass] ?: fragmentClass.java.newInstance()
    fragments[fragmentClass] = fragment
    val transaction = supportFragmentManager.beginTransaction()
    if (currentFragment != null) {
      transaction.hide(currentFragment!!)
      if (!fragment.isAdded) {
        transaction.add(R.id.fragment_container, fragment)
      } else {
        transaction.show(fragment)
      }
    } else {
      transaction.replace(R.id.fragment_container, fragment)
    }
    transaction.commit()
    currentFragment = fragment
  }
}
