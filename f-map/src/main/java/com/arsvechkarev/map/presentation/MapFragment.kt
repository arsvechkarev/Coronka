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
import com.arsvechkarev.map.presentation.MapScreenState.FoundCountry
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountries
import com.arsvechkarev.map.presentation.MapScreenState.LoadingCountryInfo
import core.ApplicationConfig
import core.FontManager
import core.Loggable
import core.StateHandle
import core.extenstions.invisible
import core.extenstions.visible
import core.log
import kotlinx.android.synthetic.main.fragment_map.bottomSheet
import kotlinx.android.synthetic.main.fragment_map.layoutLoading
import kotlinx.android.synthetic.main.fragment_map.statsView
import kotlinx.android.synthetic.main.fragment_map.textViewCountryName

class MapFragment : Fragment(R.layout.fragment_map), Loggable {
  
  override val logTag = "Map_Fragment"
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: CountriesInfoViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapDelegate.init(requireContext(), childFragmentManager,
      ::onMapClicked, ::onCountrySelected, ApplicationConfig.Threader)
    viewModel = MapModuleInjector.provideViewModel(this)
    viewModel // Allow use cache if fragment was recreated
        .requestUpdateCountriesInfo(allowUseSavedData = savedInstanceState != null)
    viewModel.state.observe(this, Observer(this::handleStateChanged))
    textViewCountryName.typeface = FontManager.rubik
    bottomSheet.setOnClickListener { bottomSheet.hide() }
  }
  
  private fun handleStateChanged(stateHandle: StateHandle<MapScreenState>) {
    log { "states = ${stateHandle.states.keys}" }
    stateHandle.handleUpdate { state ->
      when (state) {
        is LoadingCountries -> handleStartLoadingCountries()
        is CountriesLoaded -> handleCountriesLoaded(state)
        is LoadingCountryInfo -> handleStartLoadingCountryInfo()
        is FoundCountry -> handleFoundCountry(state)
        is Failure -> handleFailure(state)
      }
    }
  }
  
  private fun handleStartLoadingCountries() {
    log { "startLoadingCountries" }
    layoutLoading.visible()
  }
  
  private fun handleCountriesLoaded(state: CountriesLoaded) {
    log { "loaded countries" }
    if (!state.isfromCache) {
      layoutLoading.invisible()
    }
    mapDelegate.drawCountriesMarks(state.countriesList)
  }
  
  private fun handleStartLoadingCountryInfo() {
  }
  
  private fun handleFoundCountry(state: FoundCountry) {
    textViewCountryName.text = state.country.countryName
    statsView.updateNumbers(
      state.country.confirmed.toInt(),
      state.country.recovered.toInt(),
      state.country.deaths.toInt()
    )
    bottomSheet.show()
  }
  
  private fun handleFailure(state: Failure) {
    layoutLoading.invisible()
    Toast.makeText(context, "Failure: ${state.reason}", Toast.LENGTH_SHORT).show()
    log { "error, reason = ${state.reason}" }
  }
  
  private fun onMapClicked() {
  
  }
  
  private fun onCountrySelected(countryCode: String) {
    viewModel.findCountryByCode(countryCode)
  }
}