package com.arsvechkarev.map.presentation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import java.util.Locale


class MapDelegate {
  
  private lateinit var context: Context
  private lateinit var googleMap: GoogleMap
  
  private lateinit var onMapClicked: () -> Unit
  private lateinit var onCountrySelected: (String) -> Unit
  
  private lateinit var geocoder: Geocoder
  
  private var currentCountry = ""
  
  fun init(
    context: Context,
    fragmentManager: FragmentManager,
    onMapClicked: () -> Unit,
    onCountrySelected: (String) -> Unit
  ) {
    this.context = context
    this.onMapClicked = onMapClicked
    this.onCountrySelected = onCountrySelected
    val supportMapFragment = SupportMapFragment()
    // TODO (3/19/2020): Add other countries support
    geocoder = Geocoder(context, Locale.US)
    fragmentManager.beginTransaction()
      .replace(R.id.fragment_map_root, supportMapFragment)
      .commit()
    supportMapFragment.getMapAsync(::initMap)
  }
  
  private fun initMap(map: GoogleMap) {
    with(map) {
      googleMap = this
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context,
        R.raw.map_style
      ))
      setOnMapClickListener { latLng ->
        val addresses: List<Address> =
          geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses.isNotEmpty() && currentCountry != addresses[0].countryName) {
          currentCountry = addresses[0].countryName ?: return@setOnMapClickListener
          onCountrySelected(currentCountry)
        }
      }
    }
  }
}