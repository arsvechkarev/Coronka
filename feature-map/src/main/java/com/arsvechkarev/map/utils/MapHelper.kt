package com.arsvechkarev.map.utils

import android.content.Context
import api.threading.Threader
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import core.model.ui.CountryOnMapMetaInfo

class MapHelper(
  private val context: Context,
  mapView: MapView,
  private val onCountrySelected: (CountryOnMapMetaInfo) -> Unit,
  private val threader: Threader
) {
  
  private val mapHolder = MapHolder()
  private val countriesDrawer = CountriesDrawer(mapHolder, context)
  
  private var currentCountryMetaInfo: CountryOnMapMetaInfo? = null
  private var currentMarker: Marker? = null
  
  init {
    mapView.getMapAsync(::initMap)
  }
  
  fun drawCountries(iso2ToCountryMapMetaInfo: Map<String, CountryOnMapMetaInfo>) {
    mapHolder.execute {
      countriesDrawer.draw(iso2ToCountryMapMetaInfo)
      setOnMarkerClickListener lb@{ newMarker ->
        val newCountry = iso2ToCountryMapMetaInfo.getValue(newMarker.tag as String)
        if (currentCountryMetaInfo == newCountry) return@lb true
        countriesDrawer.drawSelection(currentMarker, currentCountryMetaInfo, newMarker, newCountry)
        currentMarker = newMarker
        currentCountryMetaInfo = newCountry
        onCountrySelected(newCountry)
        return@lb false
      }
    }
  }
  
  fun toggleMap(enable: Boolean) {
    mapHolder.execute {
      uiSettings.setAllGesturesEnabled(enable)
    }
  }
  
  private fun initMap(map: GoogleMap) {
    mapHolder.init(map)
    threader.onIoThread {
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
}
