package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import core.Application
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.fragment_container
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerGroupLinearLayout
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextMap
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextRankings
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextStatistics
import kotlinx.android.synthetic.main.partial_layout_drawer.drawerTextTips
import kotlinx.android.synthetic.main.partial_layout_drawer.navigationView

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.initDensities(resources)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    fragment_container.setOnClickListener { drawerLayout.openDrawer(navigationView) }
    goToFragment(StatsFragment())
  
    drawerTextStatistics.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextMap.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextTips.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextRankings.setOnClickListener { handleOnDrawerItemClicked(it) }
  }
  
  private fun handleOnDrawerItemClicked(view: View) {
    drawerGroupLinearLayout.onTextViewClicked(view)
    when (view) {
      drawerTextStatistics -> goToFragment(StatsFragment())
      drawerTextTips -> goToFragment(TipsFragment())
      drawerTextRankings -> goToFragment(RankingsFragment())
    }
  }
  
  private fun goToFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .commit()
  }
}
