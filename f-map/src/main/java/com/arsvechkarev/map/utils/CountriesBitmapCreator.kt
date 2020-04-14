package com.arsvechkarev.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import core.extenstions.dp
import core.model.Country
import core.model.GeneralInfo
import kotlin.random.Random

class CountriesBitmapCreator {
  
  private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = 0xAAFF8CA1.toInt()
  }
  
  private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 2.dp
    style = Paint.Style.STROKE
    color = Color.RED
  }
  
  private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = 16.dp
    color = Color.WHITE
  }
  
  private val tempRect = Rect()
  private val tempCanvas = Canvas()
  
  fun create(generalInfo: GeneralInfo, countries: List<Country>): List<MarkerOptions> {
    val generalInfoSum = generalInfo.confirmed + generalInfo.recovered + generalInfo.deaths
    val optionsList = ArrayList<MarkerOptions>(countries.size)
    for (i in countries.indices) {
      val country = countries[i]
      val bitmap = drawPicture(country, generalInfoSum)
      
      val options = MarkerOptions()
          .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
          .position(LatLng(country.latitude, country.longitude))
      optionsList.add(options)
    }
    return optionsList
  }
  
  private fun drawPicture(
    country: Country,
    totalSum: Int
  ): Bitmap {
    val confirmed = country.confirmed
    val recovered = country.recovered
    val deaths = country.deaths
    val countryTotal = confirmed + recovered + deaths
    val bitmapSize = 50 + Random.nextInt(200)
    val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
    tempCanvas.setBitmap(bitmap)
    val halfCircle = bitmapSize / 2f
    tempCanvas.drawCircle(halfCircle, halfCircle, halfCircle, circlePaint)
    tempCanvas.drawCircle(halfCircle, halfCircle, halfCircle, strokePaint)
    return bitmap
  }
  
}