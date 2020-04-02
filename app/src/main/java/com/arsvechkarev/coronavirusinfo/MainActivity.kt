package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import core.Application
import kotlinx.android.synthetic.main.activity_main.bottomNavigation

class MainActivity : AppCompatActivity() {
  
  private val mapFragment: Fragment = MapFragment()
  private val statsFragment: Fragment = StatsFragment()
  
  private var currentFragment: Fragment = mapFragment
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.Values.density = resources.displayMetrics.density
    Application.Values.scaledDensity = resources.displayMetrics.scaledDensity
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()
    savedInstanceState ?: supportFragmentManager.beginTransaction()
        .add(R.id.fragment_container, mapFragment)
        .commit()
    bottomNavigation.setOnItemClickListener(::handleOnItemClick)
  }
  
  private fun handleOnItemClick(id: Int) {
    when (id) {
      0 -> switchToFragment(mapFragment)
      1 -> switchToFragment(statsFragment)
      2 -> TODO()
    }
  }
  
  private fun switchToFragment(fragment: Fragment) {
    if (currentFragment != fragment) {
      val transaction = supportFragmentManager.beginTransaction()
      transaction.hide(currentFragment)
      if (!fragment.isAdded) transaction.add(R.id.fragment_container, fragment)
      transaction.show(fragment).commit()
      currentFragment = fragment
    }
  }
}
