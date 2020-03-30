package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.stats.presentation.StatsFragment
import core.ApplicationConfig

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ApplicationConfig.Densities.density = resources.displayMetrics.density
    ApplicationConfig.Densities.scaledDensity = resources.displayMetrics.scaledDensity
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()
    savedInstanceState ?: supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, StatsFragment())
        .commit()
  }
}
