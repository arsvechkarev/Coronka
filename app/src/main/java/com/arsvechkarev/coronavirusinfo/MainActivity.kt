package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.stats.presentation.StatsFragment
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
    Application.initResources(resources)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    fragment_container.setOnClickListener { drawerLayout.openDrawer(navigationView) }
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, StatsFragment())
        .commit()
    
    drawerTextStatistics.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextMap.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextTips.setOnClickListener { handleOnDrawerItemClicked(it) }
    drawerTextRankings.setOnClickListener { handleOnDrawerItemClicked(it) }
  }
  
  private fun handleOnDrawerItemClicked(view: View) {
    drawerGroupLinearLayout.onTextViewClicked(view)
  }
}
