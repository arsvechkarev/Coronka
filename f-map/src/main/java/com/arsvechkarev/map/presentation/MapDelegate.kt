package com.arsvechkarev.map.presentation

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import core.ApplicationConfig
import core.model.CountryInfo
import java.util.Locale


class MapDelegate {
  
  private val mapHolder = MapHolder(ApplicationConfig.Threader)
  
  private lateinit var context: Context
  
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
      mapHolder.init(this)
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context,
        R.raw.map_style
      ))
      setOnMapClickListener(::onMapClicked)
    }
  }
  
  private fun onMapClicked(latLng: LatLng) {
    val addresses: List<Address> =
        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
    addresses[0].countryCode
    if (addresses.isNotEmpty() && currentCountry != addresses[0].countryName) {
      currentCountry = addresses[0].countryName ?: return
      onCountrySelected(currentCountry)
    }
  }
  
  fun drawCountriesMarks(countriesData: List<CountryInfo>) {
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