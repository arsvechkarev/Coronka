package com.arsvechkarev.map.uils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import core.concurrency.AndroidThreader
import core.concurrency.Threader
import core.extenstions.iterate
import core.model.CountryOnMap
import core.viewbuilding.Colors.MapCircleDefault
import core.viewbuilding.Colors.MapCircleSelected
import core.viewbuilding.Colors.MapCircleStrokeDefault
import core.viewbuilding.Colors.MapCircleStrokeSelected
import kotlin.math.max
import kotlin.math.pow

class CountriesDrawer(
  private val mapHolder: MapHolder,
  context: Context,
  private val threader: Threader = AndroidThreader,
) {
  
  private val minBitmapSize: Float
  private val maxBitmapSize: Float
  private val countriesToSizes = HashMap<Int, Int>()
  private val circlePaint = Paint(ANTI_ALIAS_FLAG)
  private val strokePaint = Paint(ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.STROKE
  }
  
  init {
    val width = context.resources.displayMetrics.widthPixels
    val height = context.resources.displayMetrics.heightPixels
    maxBitmapSize = minOf(width, height) / 4f
    minBitmapSize = maxBitmapSize / 4.5f
    strokePaint.strokeWidth = minBitmapSize / 10
  }
  
  fun draw(iso2ToCountryMap: Map<String, CountryOnMap>) {
    val maxCountryOnMap = iso2ToCountryMap.values.maxByOrNull { it.country.confirmed }!!
    iso2ToCountryMap.iterate { iso2, countryOnMap ->
      val location = countryOnMap.location
      val latLng = LatLng(location.lat, location.lng)
      val bitmap = createMarkerBitmap(countryOnMap, maxCountryOnMap)
      val options = MarkerOptions()
          .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
          .position(latLng)
          .alpha(0.6f)
      mapHolder.execute {
        val marker = addMarker(options)
        marker.tag = iso2
      }
    }
  }
  
  fun drawSelection(
    oldMarker: Marker?,
    oldCountry: CountryOnMap?,
    newMarker: Marker,
    newCountry: CountryOnMap
  ) {
    if (oldMarker != null && oldCountry != null) {
      drawMarker(oldMarker, oldCountry, MapCircleDefault, MapCircleStrokeDefault)
    }
    drawMarker(newMarker, newCountry, MapCircleSelected, MapCircleStrokeSelected)
  }
  
  private fun createMarkerBitmap(
    countryOnMap: CountryOnMap,
    maxCountry: CountryOnMap
  ): Bitmap {
    val bitmapSize = transformCasesToSize(countryOnMap.country.confirmed,
      maxCountry.country.confirmed)
    countriesToSizes[countryOnMap.country.id] = bitmapSize
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    val tempCanvas = Canvas(bitmap)
    drawCircle(tempCanvas, MapCircleDefault, MapCircleStrokeDefault)
    return bitmap
  }
  
  private fun drawMarker(
    marker: Marker,
    countryOnMap: CountryOnMap,
    circleColor: Int,
    strokeColor: Int
  ) {
    val oldMarkerSize = countriesToSizes.getValue(countryOnMap.country.id)
    val bitmap = Bitmap.createBitmap(oldMarkerSize, oldMarkerSize, Bitmap.Config.ARGB_8888)
    val tempCanvas = Canvas(bitmap)
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
    val fraction = countryCases.toFloat() / mostCases.toFloat()
    val interpolatedFunction = -((1 - fraction).pow(2)) + 1
    return (maxBitmapSize * 3f * interpolatedFunction)
        .coerceIn(minBitmapSize, maxBitmapSize)
        .toInt()
  }
}
