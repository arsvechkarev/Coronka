package com.arsvechkarev.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import core.extenstions.dp
import core.model.Country
import core.model.GeneralInfo

class CountriesBitmapCreator {
  
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 5.dp
  }
  
  fun create(generalInfo: GeneralInfo, countries: List<Country>): List<GroundOverlayOptions> {
    val generalInfoSum = generalInfo.confirmed + generalInfo.recovered + generalInfo.deaths
    var canvas: Canvas
    val optionsList = ArrayList<GroundOverlayOptions>(countries.size)
    for (i in countries.indices) {
      val country = countries[i]
      val confirmed = country.confirmed
      val recovered = country.recovered
      val deaths = country.deaths
      val total = confirmed + recovered + deaths
      val confirmedAngle = 360f * confirmed / total
      val recoveredAngle = 360f * recovered / total
      val deathsAngle = 360f * deaths / total
      
      val bitmapSize = calculateBitmapSize(total, generalInfoSum)
      val bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888)
      canvas = Canvas(bitmap)
      drawCircle(canvas, confirmedAngle, recoveredAngle, deathsAngle, country)
      val options = GroundOverlayOptions()
          .image(BitmapDescriptorFactory.fromBitmap(bitmap))
          .position(LatLng(country.latitude, country.longitude), bitmap.width.toFloat(),
            bitmap.height.toFloat())
      optionsList.add(options)
    }
    return optionsList
  }
  
  private fun calculateBitmapSize(total: Int, generalInfoSum: Int): Int {
    return (total.toFloat() / generalInfoSum.toFloat() * 500000f).toInt()
  }
  
  private fun drawCircle(
    canvas: Canvas,
    confirmedAngle: Float,
    recoveredAngle: Float,
    deathsAngle: Float,
    country: Country
  ) {
    val startAngleSecond = confirmedAngle + recoveredAngle
    val width = canvas.width.toFloat()
    val height = canvas.height.toFloat()
    paint.color = Color.WHITE
    canvas.drawCircle(width / 2, height / 2, width / 2, paint)
    paint.color = Color.RED
    canvas.drawArc(0f, 0f, width, height, 0f, confirmedAngle, false, paint)
    paint.color = Color.GREEN
    canvas.drawArc(0f, 0f, width, height, confirmedAngle, startAngleSecond, false, paint)
    paint.color = Color.BLACK
    canvas.drawArc(0f, 0f, width, height, startAngleSecond,
      confirmedAngle + recoveredAngle + deathsAngle, false, paint)
  }
  
}