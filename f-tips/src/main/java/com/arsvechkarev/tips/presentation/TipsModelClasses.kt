package com.arsvechkarev.tips.presentation

import core.recycler.DisplayableItem

object MainHeader : DisplayableItem

class Header(val title: String) : DisplayableItem

class FAQItem(
  val questionLayoutRes: Int,
  val answerLayoutRes: Int
) : DisplayableItem

object SymptomsLayout : DisplayableItem

class PreventionItem(
  val imageRes: Int,
  val title: String
) : DisplayableItem
