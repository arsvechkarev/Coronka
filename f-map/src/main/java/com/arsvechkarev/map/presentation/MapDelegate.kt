package com.arsvechkarev.map.presentation

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.fragment.app.FragmentManager
import com.arsvechkarev.map.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import core.Colors
import core.concurrency.AndroidThreader
import core.extenstions.lerpColor
import core.model.Country
import core.model.CountryOnMap
import kotlin.math.pow

class MapDelegate {
  
  private val mapHolder = MapHolder()
  private val countriesDrawer = CountriesDrawer()
  private val threader = AndroidThreader
  
  private lateinit var context: Context
  private lateinit var onCountrySelected: (Country) -> Unit
  
  private var countriesMap = HashMap<String, Country>()
  
  fun init(context: Context, manager: FragmentManager, onCountrySelected: (Country) -> Unit) {
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
      mapHolder.init(this)
      setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
      uiSettings.isMapToolbarEnabled = false
      uiSettings.isRotateGesturesEnabled = false
      uiSettings.isMyLocationButtonEnabled = false
      setMaxZoomPreference(4f)
    }
  }
  
  fun drawCountries(countries: List<Country>) {
    //    threader.onBackground {
    //      val mapCountries = getMapCountries(countries.toMutableList())
    //      mapHolder.addAction { map ->
    //        val layer = GeoJsonLayer(map, R.raw.countries, context)
    //        layer.features.forEach { feature ->
    //          feature.polygonStyle = GeoJsonPolygonStyle().apply {
    //            val countryOnMap = mapCountries.find { it.iso2 == feature.getProperty("iso_a2") }
    //            val color = countryOnMap?.color ?: Color.TRANSPARENT
    //            fillColor = color
    //            strokeWidth = STROKE_WIDTH
    //          }
    //        }
    //        layer.setOnFeatureClickListener { feature ->
    //          val country = countriesMap[feature.getProperty("iso_a2")] ?: return@setOnFeatureClickListener
    //          onCountrySelected(country)
    //          countriesDrawer.drawSelection(feature as GeoJsonFeature)
    //        }
    //        threader.onMainThread {
    //          layer.addLayerToMap()
    //        }
    //      }
    //    }
  }
  
  @WorkerThread
  fun getMapCountries(countries: MutableList<Country>): List<CountryOnMap> {
    countries.sortBy { it.confirmed }
    val mapCountries = ArrayList<CountryOnMap>()
    val size = countries.size
    for (i in countries.indices) {
      val country = countries[i]
      countriesMap[country.iso2] = country
      val fraction = (i.toFloat() / size).normalize()
      val normalizedFraction = fraction.normalize()
      val color = lerpColor(Colors.landscape, Colors.mostInfectedCountry, normalizedFraction)
      mapCountries.add(CountryOnMap(country.iso2, color))
    }
    return mapCountries
  }
  
  private fun Float.normalize(): Float {
    return this.pow(2.5f)
  }
}
