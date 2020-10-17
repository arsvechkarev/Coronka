package com.arsvechkarev.coronavirusinfo

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.tips.presentation.TipsFragment
import core.BaseFragment
import core.HostActivity
import core.concurrency.AndroidThreader
import core.extenstions.connectivityManager
import core.viewbuilding.Colors
import kotlinx.android.synthetic.main.activity_main.drawerGroupLinearLayout
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.drawerTextMap
import kotlinx.android.synthetic.main.activity_main.drawerTextNews
import kotlinx.android.synthetic.main.activity_main.drawerTextRankings
import kotlinx.android.synthetic.main.activity_main.drawerTextStatistics
import kotlinx.android.synthetic.main.activity_main.drawerTextTips
import viewdsl.Densities
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity(), HostActivity {
  
  private val fragments = HashMap<KClass<*>, BaseFragment>()
  private var currentFragment: BaseFragment? = null
  
  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    
    override fun onAvailable(network: Network) {
      AndroidThreader.onMainThread { currentFragment?.onNetworkAvailable() }
    }
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    val onDrawerItemClick: (v: View) -> Unit = { handleOnDrawerItemClicked(it) }
    drawerTextStatistics.setOnClickListener(onDrawerItemClick)
    drawerTextNews.setOnClickListener(onDrawerItemClick)
    drawerTextMap.setOnClickListener(onDrawerItemClick)
    drawerTextTips.setOnClickListener(onDrawerItemClick)
    drawerTextRankings.setOnClickListener(onDrawerItemClick)
    drawerTextNews.isSelected = true
    if (savedInstanceState == null) {
      goToFragment(TipsFragment::class)
    }
  }
  
  override fun onStart() {
    super.onStart()
    connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
  }
  
  override fun onStop() {
    super.onStop()
    connectivityManager.unregisterNetworkCallback(networkCallback)
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
  
  private fun handleOnDrawerItemClicked(view: View) {
    drawerGroupLinearLayout.onTextViewClicked(view)
    drawerLayout.close(andThen = {
      when (view) {
        drawerTextStatistics -> goToFragment(StatsFragment::class)
        drawerTextNews -> goToFragment(NewsFragment::class)
        drawerTextMap -> goToFragment(MapFragment::class)
        drawerTextTips -> goToFragment(TipsFragment::class)
        drawerTextRankings -> goToFragment(RankingsFragment::class)
      }
    })
  }
  
  private fun goToFragment(fragmentClass: KClass<out BaseFragment>) {
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
}
