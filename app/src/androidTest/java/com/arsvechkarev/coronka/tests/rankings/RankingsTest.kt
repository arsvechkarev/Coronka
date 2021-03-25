package com.arsvechkarev.coronka.tests.rankings

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import base.extensions.formatRankingsNumber
import base.extensions.toFormattedNumber
import base.views.SmallStatsView
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.coronka.clickAndWaitForIdle
import com.arsvechkarev.coronka.configureDurationsAndDelaysForTests
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screen
import com.arsvechkarev.coronka.screens.DrawerScreen
import com.arsvechkarev.coronka.screens.RankingsScreen
import com.arsvechkarev.coronka.screens.StatsScreen
import com.arsvechkarev.rankings.di.RankingsModule
import core.di.ModuleInterceptorManager
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RankingsTest {
  
  companion object {
    
    @JvmStatic
    @BeforeClass
    fun setup() {
      configureDurationsAndDelaysForTests()
      ModuleInterceptorManager.addInterceptorForModule<RankingsModule> { FakeRankingsModule }
    }
  }
  
  @get:Rule
  val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)
  
  @Test
  fun test_displaying_ranks() {
    val allCountries = DataProvider.getAllCountries()
    val countryWithMostCases = allCountries.maxByOrNull { it.confirmed }!!
    
    screen<StatsScreen>().iconDrawer.clickAndWaitForIdle()
    
    screen<DrawerScreen>().textRankings.clickAndWaitForIdle()
    
    onScreen<RankingsScreen> {
      recyclerCountries {
        hasSize(allCountries.size)
        hasItemViewAt(0) lb@{ itemView ->
          require(itemView is SmallStatsView)
          return@lb itemView.number == (1.formatRankingsNumber())
              && itemView.amount == (countryWithMostCases.confirmed.toFormattedNumber())
        }
      }
    }
  }
  
  @Test
  fun test_filtering() {
    val allCountries = DataProvider.getAllCountries()
    val countryWithMostRecovered = allCountries.maxByOrNull { it.recovered }!!
    val countryWithLeastRecovered = allCountries.minByOrNull { it.recovered }!!
  
    screen<StatsScreen>().iconDrawer.clickAndWaitForIdle()
  
    screen<DrawerScreen>().textRankings.clickAndWaitForIdle()
    
    onScreen<RankingsScreen> {
      fabFilter.clickAndWaitForIdle()
      bottomSheet.isDisplayed()
      bottomSheetCross.clickAndWaitForIdle()
      bottomSheet.isNotDisplayed()
      fabFilter.clickAndWaitForIdle()
  
      chipRecovered.clickAndWaitForIdle()
      
      recyclerCountries {
        hasSize(allCountries.size)
        hasItemViewAt(0) lb@{ itemView ->
          require(itemView is SmallStatsView)
          return@lb itemView.number == (1.formatRankingsNumber())
              && itemView.amount == (countryWithMostRecovered.recovered.toFormattedNumber())
        }
        hasItemViewAt(allCountries.lastIndex) lb@{ itemView ->
          require(itemView is SmallStatsView)
          return@lb itemView.number == (allCountries.size.formatRankingsNumber())
              && itemView.amount == (countryWithLeastRecovered.recovered.toFormattedNumber())
        }
      }
  
      chipEurope.clickAndWaitForIdle()
      
      recyclerCountries.hasSizeLessThan(allCountries.size)
  
      chipWorldwide.clickAndWaitForIdle()
      
      recyclerCountries.hasSize(allCountries.size)
    }
  }
}