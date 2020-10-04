package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import core.Application
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerGroupLinearLayout
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextMap
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextRankings
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextStatistics
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextTips
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {
  
  private val fragments = HashMap<KClass<*>, Fragment>()
  private var currentFragment: Fragment? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.initDensities(resources)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    goToFragment(RankingsFragment::class)
    drawerTextStatistics.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextMap.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextTips.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextRankings.setOnClickListener { handleOnDrawerItemClicked(it) }
  }
  
  private fun handleOnDrawerItemClicked(view: View) {
    drawerGroupLinearLayout.onTextViewClicked(view)
    when (view) {
      drawerTextStatistics -> goToFragment(StatsFragment::class)
      drawerTextTips -> goToFragment(TipsFragment::class)
      drawerTextRankings -> goToFragment(RankingsFragment::class)
    }
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
