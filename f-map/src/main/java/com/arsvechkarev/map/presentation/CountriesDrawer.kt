package com.arsvechkarev.map.presentation

import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle

class CountriesDrawer {
  
  private var currentFeature: GeoJsonFeature? = null
  
  fun drawSelection(newFeature: GeoJsonFeature) {
    if (currentFeature != null) {
      drawMarkerByCountry(currentFeature!!, newFeature.polygonStyle.fillColor)
    }
    currentFeature = newFeature
    drawMarkerByCountry(newFeature, SELECTED_FILL_COLOR)
  }
  
  private fun drawMarkerByCountry(feature: GeoJsonFeature, fillColor: Int) {
    feature.polygonStyle = GeoJsonPolygonStyle().apply {
      setFillColor(fillColor)
      strokeWidth = STROKE_WIDTH
    }
  }
  
  companion object {
    const val STROKE_WIDTH = 3f
    private const val SELECTED_FILL_COLOR = 0xFF007FFF.toInt()
  }
}
