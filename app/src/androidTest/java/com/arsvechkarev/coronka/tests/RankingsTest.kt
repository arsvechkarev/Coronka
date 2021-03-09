package com.arsvechkarev.coronka.tests

import android.os.SystemClock.sleep
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screen
import com.arsvechkarev.coronka.screens.DrawerScreen
import com.arsvechkarev.coronka.screens.RankingsScreen
import com.arsvechkarev.coronka.screens.StatsScreen
import com.arsvechkarev.views.SmallStatsView
import core.extenstions.assertThat
import core.extenstions.formatRankingsNumber
import core.extenstions.toFormattedNumber
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RankingsTest {
  
  @get:Rule
  val rule = ActivityTestRule(MainActivity::class.java)
  
  @Test
  fun test_displaying_ranks() {
    val allCountries = DataProvider.getTotalData().countries
    val countryWithMostCases = allCountries.maxByOrNull { it.confirmed }!!
    
    screen<StatsScreen>().iconDrawer.click()
    
    screen<DrawerScreen>().textRankings.click()
    
    sleep(1000)
    
    onScreen<RankingsScreen> {
      recyclerCountries {
        hasSize(allCountries.size)
        hasItemViewAt(0) lb@{ itemView ->
          assertThat(itemView is SmallStatsView)
          return@lb itemView.number == (1.formatRankingsNumber())
              && itemView.amount == (countryWithMostCases.confirmed.toFormattedNumber())
        }
      }
    }
  }
  
  @Test
  fun test_filtering() {
    val allCountries = DataProvider.getTotalData().countries
    val countryWithMostRecovered = allCountries.maxByOrNull { it.recovered }!!
    val countryWithLeastRecovered = allCountries.minByOrNull { it.recovered }!!
    
    screen<StatsScreen>().iconDrawer.click()
    
    screen<DrawerScreen>().textRankings.click()
    
    sleep(1000)
    
    onScreen<RankingsScreen> {
      fabFilter.click()
      bottomSheet.isDisplayed()
      bottomSheetCross.click()
      bottomSheet.isNotDisplayed()
      fabFilter.click()
      
      chipRecovered.click()
      
      recyclerCountries {
        hasSize(allCountries.size)
        hasItemViewAt(0) lb@{ itemView ->
          assertThat(itemView is SmallStatsView)
          return@lb itemView.number == (1.formatRankingsNumber())
              && itemView.amount == (countryWithMostRecovered.recovered.toFormattedNumber())
        }
        hasItemViewAt(allCountries.lastIndex) lb@{ itemView ->
          assertThat(itemView is SmallStatsView)
          return@lb itemView.number == (allCountries.size.formatRankingsNumber())
              && itemView.amount == (countryWithLeastRecovered.recovered.toFormattedNumber())
        }
      }
      
      chipEurope.click()
      
      recyclerCountries.hasSizeLessThan(allCountries.size)
      
      chipWorldwide.click()
      
      recyclerCountries.hasSize(allCountries.size)
    }
  }
}