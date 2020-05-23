package core

import android.content.Context
import androidx.core.content.ContextCompat
import com.arsvechkarev.core.R

object Colors {
  
  const val landscape = 0xff11568a.toInt()
  const val mostInfectedCountry = 0xff041854.toInt()
  
  var confirmedColor: Int = -1
    private set
  var deathsColor: Int = -1
    private set
  var recoveredColor: Int = -1
    private set
  
  fun setup(context: Context) {
    confirmedColor = ContextCompat.getColor(context, R.color.dark_confirmed)
    deathsColor = ContextCompat.getColor(context, R.color.dark_deaths_2)
    recoveredColor = ContextCompat.getColor(context, R.color.dark_recovered)
  }
}