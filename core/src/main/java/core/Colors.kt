package core

import android.content.Context
import androidx.core.content.ContextCompat
import com.arsvechkarev.core.R

object Colors {
  
  var confirmedColor: Int = -1
    private set
  var deathsColor: Int = -1
    private set
  var recoveredColor: Int = -1
    private set
  
  fun setup(context: Context) {
    confirmedColor = ContextCompat.getColor(context, R.color.dark_confirmed)
    deathsColor = ContextCompat.getColor(context, R.color.dark_deaths)
    recoveredColor = ContextCompat.getColor(context, R.color.dark_recovered)
  }
}