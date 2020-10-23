package com.arsvechkarev.coronka.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.arsvechkarev.coronka.ConnectivityObserver
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.di.MainModuleInjector
import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.viewdsl.Densities
import core.BaseScreenState
import core.HostActivity
import core.extenstions.connectivityManager
import core.navigation.Navigator
import core.viewbuilding.Colors
import kotlinx.android.synthetic.main.activity_main.drawerGroupLinearLayout
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.drawerTextMap
import kotlinx.android.synthetic.main.activity_main.drawerTextNews
import kotlinx.android.synthetic.main.activity_main.drawerTextRankings
import kotlinx.android.synthetic.main.activity_main.drawerTextStatistics
import kotlinx.android.synthetic.main.activity_main.drawerTextTips

class MainActivity : AppCompatActivity(), HostActivity {
  
  private lateinit var viewModel: MainViewModel
  private lateinit var navigator: Navigator
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    navigator = MainModuleInjector.provideNavigator(this)
    viewModel = MainModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.figureOutScreenToGo(intent, savedInstanceState)
    }
    val connectivityObserver = ConnectivityObserver(connectivityManager, onNetworkAvailable = {
      navigator.currentFragment?.onNetworkAvailable()
    })
    lifecycle.addObserver(connectivityObserver)
    lifecycle.addObserver(navigator)
    initListeners()
  }
  
  override fun openDrawer() {
    drawerLayout.open()
  }
  
  override fun enableTouchesOnDrawer() {
    drawerLayout.respondToTouches = true
  }
  
  override fun disableTouchesOnDrawer() {
    drawerLayout.respondToTouches = false
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is GoToRegistrationScreen -> {
        drawerLayout.respondToTouches = false
        navigator.navigateTo(RegistrationFragment::class)
      }
      is GoToMainScreen -> goToMainFragment()
      is ShowEmailLinkLoading -> {
        Toast.makeText(this, "Loading email link", Toast.LENGTH_LONG).show()
      }
      is SuccessfullySignedId -> {
        Toast.makeText(this, "Successfully signed in", Toast.LENGTH_LONG).show()
        goToMainFragment()
      }
    }
  }
  
  private fun goToMainFragment() {
    drawerLayout.respondToTouches = true
    drawerTextStatistics.isSelected = true
    navigator.navigateTo(StatsFragment::class)
  }
  
  private fun initListeners() {
    val onDrawerItemClick: (v: View) -> Unit = { view ->
      drawerGroupLinearLayout.onTextViewClicked(view)
      navigator.handleOnDrawerItemClicked(view.id)
    }
    drawerTextStatistics.setOnClickListener(onDrawerItemClick)
    drawerTextNews.setOnClickListener(onDrawerItemClick)
    drawerTextMap.setOnClickListener(onDrawerItemClick)
    drawerTextTips.setOnClickListener(onDrawerItemClick)
    drawerTextRankings.setOnClickListener(onDrawerItemClick)
  }
}