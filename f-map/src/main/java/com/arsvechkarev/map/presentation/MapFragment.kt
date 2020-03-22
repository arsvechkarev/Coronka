package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.Failure

class MapFragment : Fragment(R.layout.fragment_map) {
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: CountriesInfoViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager, ::onMapClicked, ::onCountrySelected)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel // Allow use cache if fragment was recreated
        .requestUpdateCountriesInfo(allowUseCache = savedInstanceState != null)
    viewModel.state.observe(this, Observer(this::handleState))
  }
  
  private fun handleState(state: MapScreenState) {
    when (state) {
      is CountriesLoaded -> {
        mapDelegate.drawCountriesMarks(state.countriesList)
      }
      is Failure -> {
        Toast.makeText(context, "Failure while loading", Toast.LENGTH_SHORT).show()
      }
    }
  }
  
  private fun onMapClicked() {
  
  }
  
  private fun onCountrySelected(countryCode: String) {
    val country = viewModel.findCountryByCode(countryCode)
    Toast.makeText(context, countryCode, Toast.LENGTH_SHORT).show()
  }
}