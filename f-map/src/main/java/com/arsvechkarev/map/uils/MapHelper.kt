package com.arsvechkarev.map.uils

import android.content.Context
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import core.AndroidSchedulers
import core.Schedulers
import core.model.Country
import core.model.CountryOnMap

class MapHelper(
  private val context: Context,
  mapView: MapView,
  private val onCountrySelected: (Country) -> Unit,
  private val schedulers: Schedulers = AndroidSchedulers
) {
  
  private val mapHolder = MapHolder()
  private val countriesDrawer = CountriesDrawer(mapHolder, context)
  
  private var currentCountry: CountryOnMap? = null
  private var currentMarker: Marker? = null
  
  init {
    mapView.getMapAsync(::initMap)
  }
  
  fun drawCountries(iso2ToCountryMap: Map<String, CountryOnMap>) {
    mapHolder.execute {
      countriesDrawer.draw(iso2ToCountryMap)
      setOnMarkerClickListener lb@{ newMarker ->
        val newCountry = iso2ToCountryMap.getValue(newMarker.tag as String)
        if (currentCountry == newCountry) return@lb true
        countriesDrawer.drawSelection(currentMarker, currentCountry, newMarker, newCountry)
        currentMarker = newMarker
        currentCountry = newCountry
        onCountrySelected(newCountry.country)
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
    schedulers.io().scheduleDirect {
      val loadRawResourceStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
      schedulers.mainThread().scheduleDirect {
        map.setMapStyle(loadRawResourceStyle)
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        map.setMaxZoomPreference(4f)
      }
    }
  }
}
