package com.arsvechkarev.coronka.tests

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.coronka.fakeapi.FakeAlwaysSuccessNetworker.getStringByUrl
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screens.StatsScreen
import core.toGeneralInfo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class StartTest {
  
  @get:Rule
  val chain: RuleChain = RuleChain.outerRule(ActivityTestRule(MainActivity::class.java))
  
  @Test
  fun test() {
    val generalInfo = getStringByUrl(GeneralInfoRepository.URL).toGeneralInfo()
    onScreen<StatsScreen> {
      statsIconDrawer {
        isVisible()
      }
      statsGeneralStatsView {
        hasGeneralInfo(generalInfo)
      }
    }
  }
}