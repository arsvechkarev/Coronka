package com.arsvechkarev.coronavirusinfo

import android.os.Bundle
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.map.MapFragment

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS);
    setContentView(R.layout.activity_main)
    supportActionBar?.hide()
    savedInstanceState ?: supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, MapFragment())
        .commit()
    
    //    button.setOnClickListener {
    //      thread {
    //        val client = OkHttpClient()
    //
    //        val request: Request = Request.Builder()
    //            .url("https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest")
    //            .build()
    //
    //        val response = client.newCall(request).execute()
    //        val string = response.body?.string()
    //        val json = JSONArray(string!!)
    //        repeat(json.length()) {
    //          if (json.getJSONObject(it)["provincestate"].toString().isEmpty()) {
    //            println("$it = ${json.getJSONObject(it)["countryregion"]} = ${json.getJSONObject(it)["confirmed"]}")
    //          }
    //        }
    //      }
    //    }
    
  }
}
