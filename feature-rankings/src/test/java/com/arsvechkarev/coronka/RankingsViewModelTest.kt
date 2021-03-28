package com.arsvechkarev.coronka

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import api.recycler.DifferentiableItem
import com.arsvechkarev.rankings.domain.CountriesFilterer
import com.arsvechkarev.rankings.domain.RankingsInteractor
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import com.arsvechkarev.rankings.presentation.RankingsViewModel.Companion.DefaultOptionType
import com.arsvechkarev.rankings.presentation.RankingsViewModel.Companion.DefaultWorldRegion
import com.arsvechkarev.rankings.presentation.Success
import com.arsvechkarev.test.FakeCountries
import com.arsvechkarev.test.FakeCountriesDataSource
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import com.arsvechkarev.test.FakeScreenStateObserver
import com.arsvechkarev.test.currentState
import com.arsvechkarev.test.hasStateAtPosition
import com.arsvechkarev.test.hasStatesCount
import com.arsvechkarev.test.state
import config.RxConfigurator
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.Loading
import core.model.OptionType
import core.model.OptionType.RECOVERED
import core.model.WorldRegion
import core.model.WorldRegion.EUROPE
import core.model.data.CountryMetaInfo
import core.model.domain.Country
import core.model.mappers.CountryEntitiesToCountriesMapper
import core.model.ui.DisplayableCountry
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class RankingsViewModelTest {
  
  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()
  
  private val maxRetryCount = 3
  
  @Before
  fun setUp() {
    RxConfigurator.configureNetworkDelay(0)
    RxConfigurator.configureRetryCount(maxRetryCount.toLong())
  }
  
  @Test
  fun `Basic flow`() {
    val viewModel = createViewModel()
    val observer = createObserver()
    val countriesFilterer = CountriesFilterer()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStatesCount(2)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Success>(1)
      val expectedCountries = countriesFilterer.prepareAndFilter(FakeCountries, FakeMetaInfoMap,
        DefaultWorldRegion, DefaultOptionType)
      val loadedCountries = currentState<Success>().countries
      assertArrayEquals(expectedCountries.toTypedArray(), loadedCountries.toTypedArray())
    }
  }
  
  @Test
  fun `Testing filter dialog states`() {
    val viewModel = createViewModel()
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.onFilterDialogShow()
    
    with(observer) {
      hasStatesCount(3)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Success>(1).apply {
        assertFalse(showFilterDialog)
      }
      hasStateAtPosition<Success>(2).apply {
        assertTrue(showFilterDialog)
      }
    }
  }
  
  @Test
  fun `Error handling`() {
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = TimeoutException()
    )
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStatesCount(2)
      hasStateAtPosition<Loading>(0)
      assertEquals(TIMEOUT, currentState<Failure>().reason)
    }
  }
  
  @Test
  fun `Testing retry`() {
    val countriesFilterer = CountriesFilterer()
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException()
    )
    val observer = createObserver()
  
    val initialFilteredList = countriesFilterer.prepareAndFilter(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.retryLoadingData()
  
    with(observer) {
      hasStatesCount(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Failure>(1)
      hasStateAtPosition<Loading>(2)
      hasStateAtPosition<Success>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val list = currentState<Success>().countries
      assertArrayEquals(initialFilteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  
  @Test
  fun `Testing network availability callback`() {
    val countriesFilterer = CountriesFilterer()
    val notifier = FakeNetworkAvailabilityNotifier()
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException(),
      notifier = notifier
    )
    val observer = createObserver()
  
    val initialFilteredList = countriesFilterer.prepareAndFilter(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    notifier.notifyNetworkAvailable()
    
    with(observer) {
      hasStatesCount(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Failure>(1)
      hasStateAtPosition<Loading>(2)
      hasStateAtPosition<Success>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val list = currentState<Success>().countries
      assertArrayEquals(initialFilteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  @Test
  fun `Test filtering`() {
    val countriesFilterer = CountriesFilterer()
    val viewModel = createViewModel()
    val observer = createObserver()
    val worldRegion = EUROPE
    val optionType = RECOVERED
  
    val filteredList = countriesFilterer.prepareAndFilter(
      FakeCountries, FakeMetaInfoMap, worldRegion, optionType
    )
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.onNewOptionTypeSelected(optionType)
    viewModel.onNewWorldRegionSelected(worldRegion)
  
    with(observer) {
      hasStatesCount(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Success>(1)
      hasStateAtPosition<Success>(2)
      hasStateAtPosition<Success>(3)
      val list = currentState<Success>().countries
      assertArrayEquals(filteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  @Test
  fun `Test showing country`() {
    val countriesFilterer = CountriesFilterer()
    val viewModel = createViewModel()
    val observer = createObserver()
  
    val filteredList = countriesFilterer.prepareAndFilter(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
    val countryIndex = 5
    val countryToClick = filteredList[countryIndex] as DisplayableCountry
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.onCountryClicked(countryToClick)
    
    with(observer) {
      hasStatesCount(3)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Success>(1)
      hasStateAtPosition<Success>(2)
      currentState<Success>().countryFullInfo!!.country == FakeCountries[countryIndex]
    }
  }
  
  private fun createViewModel(
    totalRetryCount: Int = 0,
    error: Throwable = Throwable(),
    countriesFilterer: CountriesFilterer = CountriesFilterer(),
    notifier: FakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  ): RankingsViewModel {
    val fakeCountriesDataSource = FakeCountriesDataSource(totalRetryCount, errorFactory = { error })
    val fakeCountriesMetaInfoDataSource = FakeCountriesMetaInfoDataSource()
    return RankingsViewModel(
      RankingsInteractor(
        fakeCountriesDataSource, fakeCountriesMetaInfoDataSource, countriesFilterer,
        CountryEntitiesToCountriesMapper(), FakeSchedulers
      ),
      notifier,
      FakeSchedulers
    )
  }
  
  private fun createObserver(): FakeScreenStateObserver {
    return FakeScreenStateObserver()
  }
  
  private fun CountriesFilterer.prepareAndFilter(
    countries: List<Country>,
    countriesMetaInfo: Map<String, CountryMetaInfo>,
    worldRegion: WorldRegion,
    optionType: OptionType
  ): List<DifferentiableItem> {
    prepare(countries, countriesMetaInfo)
    return filter(worldRegion, optionType)
  }
}