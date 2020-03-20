package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.CountriesInfoState.Success

class MapFragment : Fragment(R.layout.fragment_map) {
  
  private val mapDelegate = MapDelegate()
  private lateinit var countriesInfoViewModel: CountriesInfoViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager, ::onMapClicked, ::onCountrySelected)
    countriesInfoViewModel = MapModuleInjector.provideViewModel(this)
    countriesInfoViewModel.requestUpdateCountriesInfo()
    countriesInfoViewModel.countriesData.observe(this, Observer(this::handleState))
  }
  
  private fun handleState(state: CountriesInfoState) {
    when (state) {
      is Success -> {
        println("qw: fragment")
        mapDelegate.drawCountriesMarks(state.countriesData)
      }
    }
  }
  
  private fun onMapClicked() {
  
  }
  
  private fun onCountrySelected(countryName: String) {
    Toast.makeText(context, countryName, Toast.LENGTH_SHORT).show()
  }
}