package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import core.Application
import kotlinx.android.synthetic.main.activity_main.drawerLayout
import kotlinx.android.synthetic.main.activity_main.fragment_container
import kotlinx.android.synthetic.main.partial_layout_drawer.navigationView

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Application.initResources(resources)
    supportActionBar?.hide()
    setContentView(R.layout.activity_main)
    fragment_container.setOnClickListener { drawerLayout.openDrawer(navigationView) }
  }
}
