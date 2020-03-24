package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.arsvechkarev.map.R
import com.arsvechkarev.map.presentation.MapScreenState.CountriesLoaded
import com.arsvechkarev.map.presentation.MapScreenState.Failure
import com.arsvechkarev.map.presentation.MapScreenState.ShowingCountryInfo
import core.extenstions.f
import kotlinx.android.synthetic.main.fragment_map.bottomSheet
import kotlinx.android.synthetic.main.fragment_map.fragment_map_root
import kotlinx.android.synthetic.main.fragment_map.statsConfirmed

class MapFragment : Fragment(R.layout.fragment_map) {
  
  private val mapDelegate = MapDelegate()
  private lateinit var viewModel: CountriesInfoViewModel
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    
    //    mapDelegate.init(requireContext(), childFragmentManager,
    //      ::onMapClicked, ::onCountrySelected, Threader)
    //    viewModel = MapModuleInjector.provideViewModel(this)
    //    viewModel // Allow use cache if fragment was recreated
    //        .requestUpdateCountriesInfo(allowUseCache = savedInstanceState != null)
    //    viewModel.state.observe(this, Observer(this::handleState))
    statsConfirmed.setNumbers(520, 20, 6)
    var flag = true
    fragment_map_root.setOnClickListener {
      if (flag) {
        bottomSheet.show()
      } else {
        bottomSheet.hide()
      }
      flag = !flag
    }
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
  
  }
  
  private fun onCountrySelected(countryCode: String) {
    Toast.makeText(context, countryCode, Toast.LENGTH_SHORT).show()
    viewModel.findCountryByCode(countryCode)
  }
}