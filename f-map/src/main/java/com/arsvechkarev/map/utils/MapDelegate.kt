package com.arsvechkarev.map.utils

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.arsvechkarev.map.presentation.MapHolder
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import core.Application
import core.model.Country
import core.model.GeneralInfo

class MapDelegate {
  
  private val mapHolder = MapHolder()
  private val creator = CountriesBitmapCreator()
  private lateinit var context: Context
  private lateinit var onCountrySelected: (String) -> Unit
  private lateinit var threader: Application.Threader
  
  private var countries: List<Country> = ArrayList()
  private val markers = ArrayList<Marker>()
  private var currentCountryCode = ""
  
  fun init(
    context: Context,
    fragmentManager: FragmentManager,
    onCountrySelected: (String) -> Unit,
    threader: Application.Threader,
    savedInstanceState: Bundle?
  ) {
    this.context = context
    this.onCountrySelected = onCountrySelected
    this.threader = threader
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
      setOnMarkerClickListener {
        onCountrySelected(countries[it.tag as Int].iso2)
        return@setOnMarkerClickListener false
      }
    }
  }
  
  fun drawCountries(countries: List<Country>, generalInfo: GeneralInfo) {
    threader.backgroundWorker.submit {
      this.countries = countries
      markers.clear()
      val options = creator.create(generalInfo, countries)
      threader.mainThreadWorker.submit {
        mapHolder.addAction { googleMap ->
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