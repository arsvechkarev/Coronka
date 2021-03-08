package com.arsvechkarev.stats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arsvechkarev.stats.presentation.LoadedWorldCasesInfo
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.test.FakeDailyCases
import com.arsvechkarev.test.FakeGeneralInfo
import com.arsvechkarev.test.FakeGeneralInfoDataSource
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import com.arsvechkarev.test.FakeScreenStateObserver
import com.arsvechkarev.test.FakeWorldCasesInfoDataSource
import com.arsvechkarev.test.assert
import com.arsvechkarev.test.currentState
import com.arsvechkarev.test.hasCurrentState
import com.arsvechkarev.test.hasStateAtPosition
import com.arsvechkarev.test.hasStateSize
import com.arsvechkarev.test.state
import core.Failure
import core.Failure.FailureReason.NO_CONNECTION
import core.Failure.FailureReason.TIMEOUT
import core.Loading
import core.jsontransformers.WorldCasesInfoTransformer.toNewDailyCases
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class StatsViewModelTest {
  
  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()
  
  @Test
  fun `Basic flow`() {
    val viewModel = createViewModel()
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStateSize(2)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedWorldCasesInfo>(1)
      hasCurrentState<LoadedWorldCasesInfo>()
      val worldCasesInfo = state<LoadedWorldCasesInfo>(1).worldCasesInfo
      assert { worldCasesInfo.generalInfo == FakeGeneralInfo }
      assert { worldCasesInfo.totalDailyCases == FakeDailyCases }
      assert { worldCasesInfo.newDailyCases == toNewDailyCases(FakeDailyCases) }
    }
  }
  
  @Test
  fun `Error handling`() {
    val viewModel = createViewModel(totalRetryCount = 1, error = TimeoutException())
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStateSize(2)
      hasStateAtPosition<Loading>(0)
      assert { currentState<Failure>().reason == TIMEOUT }
    }
  }
  
  @Test
  fun `Testing retry`() {
    val viewModel = createViewModel(totalRetryCount = 1, error = UnknownHostException())
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData() // Initial loading
    viewModel.startLoadingData() // Retry
    
    with(observer) {
      hasStateSize(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Failure>(1)
      hasStateAtPosition<Loading>(2)
      hasStateAtPosition<LoadedWorldCasesInfo>(3)
      assert { state<Failure>(1).reason == NO_CONNECTION }
      val worldCasesInfo = currentState<LoadedWorldCasesInfo>().worldCasesInfo
      assert { worldCasesInfo.generalInfo == FakeGeneralInfo }
      assert { worldCasesInfo.totalDailyCases == FakeDailyCases }
      assert { worldCasesInfo.newDailyCases == toNewDailyCases(FakeDailyCases) }
    }
  }
  
  private fun createViewModel(
    totalRetryCount: Int = 0,
    error: Throwable = Throwable(),
    notifier: FakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  ): StatsViewModel {
    val generalInfoDataSource = FakeGeneralInfoDataSource(totalRetryCount, errorToThrow = { error })
    return StatsViewModel(
      generalInfoDataSource,
      FakeWorldCasesInfoDataSource(),
      notifier,
      FakeSchedulers
    )
  }
  
  private fun createObserver(): FakeScreenStateObserver {
    return FakeScreenStateObserver()
  }
}