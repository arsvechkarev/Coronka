package com.arsvechkarev.map.presentation

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import core.ApplicationConfig
import core.model.Country
import java.io.IOException
import java.util.Locale

class MapDelegate {
  
  private val mapHolder = MapHolder()
  private lateinit var context: Context
  private lateinit var onMapClicked: () -> Unit
  private lateinit var onCountrySelected: (String) -> Unit
  private lateinit var threader: ApplicationConfig.Threader
  private lateinit var geocoder: Geocoder
  
  private var currentCountryCode = ""
  private var circlesAreDrawn = false
  
  fun init(
    context: Context,
    fragmentManager: FragmentManager,
    onMapClicked: () -> Unit,
    onCountrySelected: (String) -> Unit,
    threader: ApplicationConfig.Threader
  ) {
    this.context = context
    this.onMapClicked = onMapClicked
    this.onCountrySelected = onCountrySelected
    this.threader = threader
    val supportMapFragment = SupportMapFragment()
    geocoder = Geocoder(context, Locale.US)
    fragmentManager.beginTransaction()
        .add(R.id.fragment_map_root, supportMapFragment)
        .commit()
    supportMapFragment.getMapAsync(::initMap)
  }
  
  private fun initMap(map: GoogleMap) {
    with(map) {
      mapHolder.init(this)
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
      setOnMapClickListener(::onMapClicked)
      uiSettings.isRotateGesturesEnabled = false
      uiSettings.isMyLocationButtonEnabled = false
      setMaxZoomPreference(6.0f)
    }
  }
  
  private fun onMapClicked(latLng: LatLng) {
    try {
      onMapClicked()
      threader.backgroundWorker.submit {
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses.isNotEmpty() && currentCountryCode != addresses[0].countryCode) {
          currentCountryCode = addresses[0].countryCode ?: return@submit
          threader.mainThreadWorker.submit {
            onCountrySelected(currentCountryCode)
          }
        }
      }
    } catch (e: IOException) {
      // Happens if geocoder
    }
  }
  
  fun drawCountriesMarksIfNeeded(countriesData: List<Country>) {
    if (circlesAreDrawn) return
    circlesAreDrawn = true
    mapHolder.addAction { googleMap ->
      for (country in countriesData) {
        googleMap.addCircle(
          CircleOptions()
              .center(LatLng(country.latitude.toDouble(), country.longitude.toDouble()))
              .radius(100000.0)
              .fillColor(Color.RED)
              .strokeColor(Color.RED)
        )
      }
    }
  }
}