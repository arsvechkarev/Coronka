package com.arsvechkarev.map.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.arsvechkarev.map.presentation.MapHolder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import core.Application
import core.model.Country

class MapDelegate {
  
  private val mapHolder = MapHolder()
  private val threader = Application.Threader
  private val creator = CountriesMarkersDrawer()
  private lateinit var context: Context
  private lateinit var onCountrySelected: (String) -> Unit
  
  private var countries: List<Country> = ArrayList()
  private var currentCountry: Country? = null
  private var currentMarker: Marker? = null
  private val markers = ArrayList<Marker>()
  
  fun init(
    context: Context,
    fragmentManager: FragmentManager,
    onCountrySelected: (String) -> Unit
  ) {
    this.context = context
    this.onCountrySelected = onCountrySelected
    val supportMapFragment = SupportMapFragment()
    fragmentManager.beginTransaction()
        .add(R.id.fragment_map_root, supportMapFragment)
        .commit()
    supportMapFragment.getMapAsync(::initMap)
  }
  
  private fun initMap(map: GoogleMap) {
    with(map) {
      uiSettings.isMapToolbarEnabled = false
      mapHolder.init(this)
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
      uiSettings.isRotateGesturesEnabled = false
      uiSettings.isMyLocationButtonEnabled = false
      setMaxZoomPreference(4.5f)
      setOnMarkerClickListener { newMarker ->
        val newCountry = countries[newMarker.tag as Int]
        creator.drawSelection(newMarker, currentMarker, newCountry, currentCountry)
        currentMarker = newMarker
        currentCountry = newCountry
        onCountrySelected(newCountry.iso2)
        return@setOnMarkerClickListener false
      }
    }
  }
  
  fun drawCountries(countries: List<Country>) {
    threader.backgroundWorker.submit {
      this.countries = countries
      markers.clear()
      val options = creator.createMarkers(countries)
      threader.mainThreadWorker.submit {
        mapHolder.addAction { googleMap ->
          googleMap.clear()
          for (i in options.indices) {
            val marker = googleMap.addMarker(options[i])
            marker.tag = i
            markers.add(marker)
          }
        }
      }
    }
  }
}