package com.arsvechkarev.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import core.model.Country
import kotlin.math.max

class CountriesMarkersDrawer {
  
  private val countriesToSizes = HashMap<Int, Int>()
  
  private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  
  private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 7f
    style = Paint.Style.STROKE
  }
  
  private val tempCanvas = Canvas()
  
  fun createMarkers(countries: List<Country>): List<MarkerOptions> {
    val countryWithMaxCases = countries.maxBy { it.confirmed }!!
    val optionsList = ArrayList<MarkerOptions>(countries.size)
    for (i in countries.indices) {
      val country = countries[i]
      val bitmap = createMarkerBitmap(country, countryWithMaxCases.confirmed)
      val options = MarkerOptions()
          .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
          .position(LatLng(country.latitude, country.longitude))
      optionsList.add(options)
    }
    return optionsList
  }
  
  fun drawSelection(newMarker: Marker, oldMarker: Marker?, newCountry: Country, oldCountry: Country?) {
    if (oldMarker != null && oldCountry != null) {
      drawMarkerByCountry(oldMarker, oldCountry, DEFAULT_CIRCLE_COLOR, DEFAULT_STROKE_COLOR)
    }
    drawMarkerByCountry(newMarker, newCountry, SELECTED_CIRCLE_COLOR, SELECTED_STROKE_COLOR)
  }
  
  private fun createMarkerBitmap(
    country: Country,
    maxCasesOfCountry: Int
  ): Bitmap {
    val bitmapSize = transformCasesToSize(country.confirmed, maxCasesOfCountry)
    countriesToSizes[country.id] = bitmapSize
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    tempCanvas.setBitmap(bitmap)
    drawCircle(tempCanvas, DEFAULT_CIRCLE_COLOR, DEFAULT_STROKE_COLOR)
    return bitmap
  }
  
  private fun drawMarkerByCountry(marker: Marker, country: Country, circleColor: Int, strokeColor: Int) {
    val oldMarkerSize = countriesToSizes[country.id]!!
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
        .coerceIn(MIN_BITMAP_SIZE, MAX_BITMAP_SIZE).toInt()
  }
  
  companion object {
    private const val MAX_BITMAP_SIZE = 380f
    private const val MIN_BITMAP_SIZE = 90f
    private const val SIZE_COEFFICIENT = MAX_BITMAP_SIZE * 3
    private const val DEFAULT_CIRCLE_COLOR = 0xAAFF8CA1.toInt()
    private const val DEFAULT_STROKE_COLOR = 0x55FF0000
    private const val SELECTED_CIRCLE_COLOR = 0xAA0000FF.toInt()
    private const val SELECTED_STROKE_COLOR = 0xCC0000FF.toInt()
  }
}
