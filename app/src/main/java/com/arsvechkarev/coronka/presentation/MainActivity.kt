package com.arsvechkarev.coronka.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import base.BaseActivity
import base.HostActivity
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
import core.di.CoreComponent.drawerStateSendingChannel

class MainActivity : BaseActivity(), HostActivity {
  
  private val navigator by lazy {
    MainModuleInjector.provideNavigator(this, DrawerLayoutTag,
      onGoToMainFragment = ::goToMainFragment,
      onFragmentAppeared = ::setSelectedMenuItem
    )
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    setupViews()
    initListeners()
    navigator.initializeWithState(savedInstanceState)
  }
  
  override fun openDrawer() {
    viewAs<DrawerLayout>(DrawerLayoutTag).open()
  }
  
  override fun enableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayoutTag).respondToTouches = true
  }
  
  override fun disableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayoutTag).respondToTouches = false
  }
  
  override fun onBackPressed() {
    val drawerLayout = viewAs<DrawerLayout>(DrawerLayoutTag)
    if (drawerLayout.state == OPENED) {
      drawerLayout.close()
      return
    }
    if (navigator.allowBackPress()) {
      super.onBackPressed()
    }
  }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    navigator.onSaveInstanceState(outState)
  }
  
  private fun setupViews() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    setContentView(buildMainActivityLayout())
    val listener = DrawerLayoutStateListener(drawerStateSendingChannel)
    viewAs<DrawerLayout>(DrawerLayoutTag).addOpenCloseListener(listener)
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
  
  private fun goToMainFragment() {
    view(TextStatistics).isSelected = true
    navigator.switchTo(StatsFragment::class)
  }
}