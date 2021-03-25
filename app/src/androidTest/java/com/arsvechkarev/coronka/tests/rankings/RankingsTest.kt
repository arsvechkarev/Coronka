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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RankingsTest {
  
  @get:Rule
  val rule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
    
    override fun afterActivityLaunched() {
      configureDurationsAndDelaysForTests()
    }
  }
  
  @Test
  fun test_displaying_ranks() {
    val allCountries = DataProvider.getAllCountriesInfo()
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
    val allCountries = DataProvider.getAllCountriesInfo()
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