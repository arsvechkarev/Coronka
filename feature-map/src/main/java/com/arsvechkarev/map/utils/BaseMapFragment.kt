package com.arsvechkarev.map.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.BaseFragment
import com.arsvechkarev.map.R
import com.google.android.gms.maps.MapView

abstract class BaseMapFragment(layoutRes: Int) : BaseFragment(layoutRes) {
  
  protected lateinit var mapView: MapView
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_map, container, false)
    mapView = view.findViewById(R.id.mapView)
    mapView.onCreate(savedInstanceState)
    return view
  }
  
  override fun onStart() {
    super.onStart()
    mapView.onStart()
  }
  
  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }
  
  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }
  
  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }
  
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
  
  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }
}