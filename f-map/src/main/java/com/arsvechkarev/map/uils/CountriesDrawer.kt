package com.arsvechkarev.map.uils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import core.extenstions.iterate
import core.model.Country
import core.model.CountryOnMap
import kotlin.math.max

class CountriesDrawer {
  
  private val countriesToSizes = HashMap<Int, Int>()
  
  private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 7f
    style = Paint.Style.STROKE
  }
  
  private val tempCanvas = Canvas()
  
  fun draw(
    map: GoogleMap,
    iso2ToCountryMap: Map<String, CountryOnMap>
  ) {
    val maxCountryOnMap = iso2ToCountryMap.values.maxByOrNull { it.country.confirmed }!!
    iso2ToCountryMap.iterate { iso2, countryOnMap ->
      val location = countryOnMap.location
      val latLng = LatLng(location.lat, location.lng)
      val bitmap = createMarkerBitmap(countryOnMap, maxCountryOnMap)
      val options = MarkerOptions()
          .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
          .position(latLng)
      val marker = map.addMarker(options)
      marker.tag = iso2
    }
  }
  
  fun drawSelection(newMarker: Marker, oldMarker: Marker?, newCountry: Country, oldCountry: Country?) {
    if (oldMarker != null && oldCountry != null) {
      drawMarker(oldMarker, oldCountry, DEFAULT_CIRCLE_COLOR, DEFAULT_STROKE_COLOR)
    }
    drawMarker(newMarker, newCountry, SELECTED_CIRCLE_COLOR, SELECTED_STROKE_COLOR)
  }
  
  private fun createMarkerBitmap(
    countryOnMap: CountryOnMap,
    maxCountry: CountryOnMap
  ): Bitmap {
    val bitmapSize = transformCasesToSize(countryOnMap.country.confirmed,
      maxCountry.country.confirmed)
    countriesToSizes[countryOnMap.country.id] = bitmapSize
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    tempCanvas.setBitmap(bitmap)
    drawCircle(tempCanvas, DEFAULT_CIRCLE_COLOR, DEFAULT_STROKE_COLOR)
    return bitmap
  }
  
  private fun drawMarker(marker: Marker, country: Country, circleColor: Int, strokeColor: Int) {
    val oldMarkerSize = countriesToSizes.getValue(country.id)
    val bitmap = Bitmap.createBitmap(oldMarkerSize, oldMarkerSize, Bitmap.Config.ARGB_8888)
    tempCanvas.setBitmap(bitmap)
    drawCircle(tempCanvas, circleColor, strokeColor)
    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
  }
  
  private fun drawCircle(canvas: Canvas, circleColor: Int, strokeColor: Int) {
    val size = max(canvas.width, canvas.height)
    val halfCircle = size / 2f
    circlePaint.color = circleColor
    canvas.drawCircle(halfCircle, halfCircle, halfCircle, circlePaint)
    strokePaint.color = strokeColor
    canvas.drawCircle(halfCircle, halfCircle, halfCircle - strokePaint.strokeWidth / 2,
      strokePaint)
  }
  
  private fun transformCasesToSize(countryCases: Int, mostCases: Int): Int {
    return (SIZE_COEFFICIENT * (countryCases.toFloat() / mostCases.toFloat()))
        .coerceIn(MIN_BITMAP_SIZE, MAX_BITMAP_SIZE)
        .toInt()
  }
  
  private companion object {
    const val MAX_BITMAP_SIZE = 380f
    const val MIN_BITMAP_SIZE = 90f
    const val SIZE_COEFFICIENT = MAX_BITMAP_SIZE * 3
    const val DEFAULT_CIRCLE_COLOR = 0xAAFF8CA1.toInt()
    const val DEFAULT_STROKE_COLOR = 0x55FF0000
    const val SELECTED_CIRCLE_COLOR = 0xE2004182.toInt()
    const val SELECTED_STROKE_COLOR = 0xFF007FFF.toInt()
  }
}
