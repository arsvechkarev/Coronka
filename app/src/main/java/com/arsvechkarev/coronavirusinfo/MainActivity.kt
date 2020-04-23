package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arsvechkarev.faq.presentation.FAQFragment
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.stats.presentation.StatsFragment
import core.Application
import kotlinx.android.synthetic.main.activity_main.bottomNavigation

class MainActivity : AppCompatActivity() {
  
  private val mapFragment: Fragment = MapFragment()
  private val statsFragment: Fragment = StatsFragment()
  private val faqFragment: Fragment = FAQFragment()
  
  private lateinit var currentFragment: Fragment
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.Values.density = resources.displayMetrics.density
    Application.Values.scaledDensity = resources.displayMetrics.scaledDensity
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .add(R.id.fragment_container, mapFragment, MapFragment::class.simpleName)
          .commit()
      currentFragment = mapFragment
    } else {
      val currentFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAGMENT)
      currentFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag)!!
    }
    bottomNavigation.setOnItemClickListener(::handleOnItemClick)
  }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(KEY_CURRENT_FRAGMENT, currentFragment::class.simpleName)
  }
  
  private fun handleOnItemClick(id: Int) {
    when (id) {
      0 -> switchToFragment(mapFragment)
      1 -> switchToFragment(statsFragment)
      2 -> switchToFragment(faqFragment)
    }
  }
  
  private fun switchToFragment(fragment: Fragment) {
    if (currentFragment != fragment) {
      val transaction = supportFragmentManager.beginTransaction()
      transaction.hide(currentFragment)
      if (!fragment.isAdded) transaction.add(R.id.fragment_container, fragment,
        fragment::class.simpleName)
      transaction.show(fragment).commit()
      currentFragment = fragment
    }
  }
  
  companion object {
    const val KEY_CURRENT_FRAGMENT = "CurrentFragment"
  }
}
