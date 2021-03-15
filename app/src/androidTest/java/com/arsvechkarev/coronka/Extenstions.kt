package com.arsvechkarev.coronka

import android.content.Context
import android.view.View
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.platform.app.InstrumentationRegistry
import com.agoda.kakao.common.actions.BaseActions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.delegate.ViewInteractionDelegate
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.viewdsl.ContextHolder
import config.AnimationsConfigurator
import config.RxConfigurator
import core.extenstions.assertThat
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

fun Any.idleMainThread() {
  InstrumentationRegistry.getInstrumentation().waitForIdleSync()
}

fun configureDurationsAndDelaysForTests() {
  RxConfigurator.configureRetryCount(0)
  RxConfigurator.configureNetworkDelay(0)
  AnimationsConfigurator.resetDurations()
}

fun ViewBuilder.withStringId(string: String) {
  val resources = ContextHolder.context.resources
  val packageName = ContextHolder.context.packageName
  withId(resources.getIdentifier(string, "id", packageName))
}

fun Context.readAssetsFile(fileName: String): String = assets.open(fileName).bufferedReader()
    .use { reader -> reader.readText() }

inline fun <reified T : Screen<T>> screen() = T::class.java.newInstance()

fun BaseActions.clickAndWaitForIdle() {
  click()
  idleMainThread()
}

inline fun <reified T : View> ViewInteractionDelegate.matches(
  description: String = "",
  crossinline matcher: (T) -> Boolean
) {
  check(ViewAssertions.matches(object : BaseMatcher<View>() {
    
    override fun describeTo(d: Description) {
      d.appendText(description)
    }
    
    override fun matches(item: Any): Boolean {
      assertThat(item is T)
      return matcher(item)
    }
  }))
}