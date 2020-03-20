package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.map.presentation.MapFragment

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS);
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()
    savedInstanceState ?: supportFragmentManager.beginTransaction()
      .replace(R.id.fragment_container, MapFragment())
      .commit()
  }
}
