package com.arsvechkarev.map.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.arsvechkarev.map.presentation.MapHolder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import core.concurrency.AndroidThreader
import core.model.Country

class MapDelegate {
  
  private val mapHolder = MapHolder()
  private val threader = AndroidThreader
  private val creator = CountriesDrawer()
  private lateinit var context: Context
  private lateinit var onCountrySelected: (String) -> Unit
  
  private var countries: List<Country> = ArrayList()
  
  fun init(context: Context, manager: FragmentManager, onCountrySelected: (String) -> Unit) {
    this.context = context
    this.onCountrySelected = onCountrySelected
    val supportMapFragment = SupportMapFragment()
    manager.beginTransaction()
        .add(R.id.fragment_map_root, supportMapFragment)
        .commit()
    supportMapFragment.getMapAsync(::initMap)
  }
  
  private fun initMap(map: GoogleMap) {
    with(map) {
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
      uiSettings.isMapToolbarEnabled = false
      mapHolder.init(this)
      uiSettings.isRotateGesturesEnabled = false
      uiSettings.isMyLocationButtonEnabled = false
      setMaxZoomPreference(4.5f)
    }
  }
  
  fun drawCountries(countries: List<Country>) {
    threader.onBackground {
      this.countries = countries
    }
  }
}