@file:Suppress("ObjectPropertyName")

package com.arsvechkarev.viewdsl

import android.content.Context

object ContextHolder {
  
  private lateinit var _context: Context
  
  val context: Context
    get() = _context
  
  fun init(context: Context) {
    _context = context
  }
}