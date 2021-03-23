package com.arsvechkarev.coronka.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import base.BaseActivity
import base.HostActivity
import base.Navigator
import base.resources.Colors
import base.views.DrawerGroupLinearLayout
import base.views.DrawerLayout
import base.views.DrawerLayout.DrawerState.OPENED
import com.arsvechkarev.coronka.di.MainModuleInjector
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import com.arsvechkarev.viewdsl.Densities

class MainActivity : BaseActivity(), HostActivity {
  
  private lateinit var navigator: Navigator
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    supportActionBar?.hide()
    setContentView(buildMainActivityLayout())
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    navigator = MainModuleInjector.provideNavigator(this, DrawerLayout) {
      setSelectedMenuItem(it)
    }
    lifecycle.addObserver(navigator)
    initListeners()
    goToMainFragment()
  }
  
  override fun openDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).open()
  }
  
  override fun enableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = true
  }
  
  override fun disableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = false
  }
  
  override fun onBackPressed() {
    val drawerLayout = viewAs<DrawerLayout>(DrawerLayout)
    if (drawerLayout.state == OPENED) {
      drawerLayout.close()
      return
    }
    if (navigator.allowBackPress()) {
      super.onBackPressed()
    }
  }
  
  private fun goToMainFragment() {
    view(TextStatistics).isSelected = true
    navigator.switchTo(StatsFragment::class)
  }
  
  private fun initListeners() {
    val onDrawerItemClick: (v: View) -> Unit = { view ->
      viewAs<DrawerGroupLinearLayout>(DrawerGroupLinearLayout).onTextViewClicked(view)
      navigator.handleOnDrawerItemClicked(view.tag as String)
    }
    view(TextStatistics).setOnClickListener(onDrawerItemClick)
    view(TextNews).setOnClickListener(onDrawerItemClick)
    view(TextMap).setOnClickListener(onDrawerItemClick)
    view(TextRankings).setOnClickListener(onDrawerItemClick)
    view(TextTips).setOnClickListener(onDrawerItemClick)
  }
  
  private fun setSelectedMenuItem(fragment: Fragment) {
    val tag = when (fragment) {
      is StatsFragment -> TextStatistics
      is NewsFragment -> TextNews
      is MapFragment -> TextMap
      is RankingsFragment -> TextRankings
      is TipsFragment -> TextTips
      else -> throw IllegalStateException()
    }
    val drawerGroupLayout = viewAs<DrawerGroupLinearLayout>(DrawerGroupLinearLayout)
    drawerGroupLayout.setSelectedMenuItem(tag)
  }
}