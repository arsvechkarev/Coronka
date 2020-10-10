package com.arsvechkarev.map.uils

import android.content.Context
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import core.concurrency.AndroidThreader
import core.model.Country
import core.model.CountryOnMap

class MapHelper {
  
  private val mapHolder = MapHolder()
  private val countriesDrawer = CountriesDrawer()
  private val threader = AndroidThreader
  
  private lateinit var mapView: MapView
  private lateinit var context: Context
  private lateinit var onCountrySelected: (Country) -> Unit
  
  private var currentCountry: Country? = null
  private var currentMarker: Marker? = null
  
  fun init(
    context: Context,
    mapView: MapView,
    onCountrySelected: (Country) -> Unit
  ) {
    this.context = context
    this.mapView = mapView
    this.onCountrySelected = onCountrySelected
    mapView.getMapAsync(::initMap)
  }
  
  private fun initMap(map: GoogleMap) {
    mapHolder.init(map)
    threader.onBackground {
      val loadRawResourceStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
      threader.onMainThread {
        map.setMapStyle(loadRawResourceStyle)
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        map.setMaxZoomPreference(4f)
      }
    }
  }
  
  fun drawCountries(
    iso2ToCountryMap: Map<String, CountryOnMap>
  ) {
    mapHolder.execute { map ->
      countriesDrawer.draw(map, iso2ToCountryMap)
      map.setOnMarkerClickListener { newMarker ->
        val newCountry = iso2ToCountryMap[newMarker.tag]!!.country
        countriesDrawer.drawSelection(newMarker, currentMarker, newCountry, currentCountry)
        currentMarker = newMarker
        currentCountry = newCountry
        onCountrySelected(newCountry)
        return@setOnMarkerClickListener false
      }
    }
  }
}
