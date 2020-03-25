package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.MapScreenState.*
import core.ApplicationConfig
import core.FontManager
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(R.layout.fragment_map) {
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: CountriesInfoViewModel
  
  var flag = true
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager,
      ::onMapClicked, ::onCountrySelected, ApplicationConfig.Threader)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel // Allow use cache if fragment was recreated
        .requestUpdateCountriesInfo(allowUseCache = savedInstanceState != null)
    viewModel.state.observe(this, Observer(this::handleState))
    textViewCountryName.typeface = FontManager.rubik
    statsView.setNumbers(100, 20, 6)
  }
  
  private fun handleState(state: MapScreenState) {
    when (state) {
      is CountriesLoaded -> {
        mapDelegate.drawCountriesMarksIfNeeded(state.countriesList)
      }
      is ShowingCountryInfo -> {
        mapDelegate.drawCountriesMarksIfNeeded(state.countriesList)
        
      }
      is Failure -> {
        Toast.makeText(context, "Failure while loading", Toast.LENGTH_SHORT).show()
      }
    }
  }
  
  private fun onMapClicked() {
    if (flag) {
      bottomSheet.show()
    } else {
      bottomSheet.hide()
    }
    flag = !flag
  }
  
  private fun onCountrySelected(countryCode: String) {
    Toast.makeText(context, countryCode, Toast.LENGTH_SHORT).show()
    viewModel.findCountryByCode(countryCode)
  }
}