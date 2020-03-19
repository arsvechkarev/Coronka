package com.arsvechkarev.map

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

class MapFragment : Fragment(R.layout.fragment_map) {
  
  private val mapDelegate = MapDelegate()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager, ::onMapClicked, ::onCountrySelected)
  }
  
  private fun onMapClicked() {
  
  }
  
  private fun onCountrySelected(countryName: String) {
    Toast.makeText(context, countryName, Toast.LENGTH_SHORT).show()
  }
}