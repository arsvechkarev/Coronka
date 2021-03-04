package com.arsvechkarev.coronka

import android.content.Context
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.screen.Screen
import com.arsvechkarev.viewdsl.ContextHolder

fun ViewBuilder.withStringId(string: String) {
  val resources = ContextHolder.context.resources
  val packageName = ContextHolder.context.packageName
  withId(resources.getIdentifier(string, "id", packageName))
}

inline fun <reified T : Screen<T>> screen() = T::class.java.newInstance()

fun Context.readAssetsFile(fileName: String): String = assets.open(fileName).bufferedReader()
    .use { reader -> reader.readText() }