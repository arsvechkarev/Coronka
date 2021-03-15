package com.arsvechkarev.tips.presentation

import core.recycler.DisplayableItem

object MainItem : DisplayableItem

class HeaderItem(val title: String) : DisplayableItem

class FAQItem(
  val questionLayoutRes: Int,
  val answerLayoutRes: Int
) : DisplayableItem

object SymptomsItem : DisplayableItem

class PreventionItem(
  val imageRes: Int,
  val textRes: Int
) : DisplayableItem
