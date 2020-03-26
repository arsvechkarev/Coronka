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
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager,
      ::onMapClicked, ::onCountrySelected, ApplicationConfig.Threader)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel // Allow use cache if fragment was recreated
        .requestUpdateCountriesInfo(allowUseSavedData = savedInstanceState != null)
    viewModel.state.observe(this, Observer(this::handleState))
    textViewCountryName.typeface = FontManager.rubik
    bottomSheet.setOnClickListener { bottomSheet.hide() }
  }
  
  private fun handleState(state: MapScreenState) {
    when (state) {
      is CountriesLoaded -> {
        mapDelegate.drawCountriesMarksIfNeeded(state.countriesList)
      }
      is ShowingCountryInfo -> {
        textViewCountryName.text = state.country.countryName
        statsView.updateNumbers(
          state.country.confirmed.toInt(),
          state.country.recovered.toInt(),
          state.country.deaths.toInt()
        )
        mapDelegate.drawCountriesMarksIfNeeded(state.countriesList)
        bottomSheet.show()
      }
      is Failure -> {
        Toast.makeText(context, "Failure while loading", Toast.LENGTH_SHORT).show()
      }
    }
  }
  
  private fun onMapClicked() {
    bottomSheet.show()
  }
  
  private fun onCountrySelected(countryCode: String) {
    viewModel.findCountryByCode(countryCode)
  }
}