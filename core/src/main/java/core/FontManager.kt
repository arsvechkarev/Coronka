package core

import android.content.Context
import android.graphics.Typeface

object FontManager {
  
  lateinit var rubik: Typeface
  
  fun init(context: Context) {
    rubik = Typeface.createFromAsset(context.assets, "rubik_medium.ttf");
  }
  
}