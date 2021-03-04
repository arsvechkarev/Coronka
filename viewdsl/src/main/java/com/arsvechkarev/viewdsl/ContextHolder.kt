@file:Suppress("ObjectPropertyName")

package com.arsvechkarev.viewdsl

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak") // Storing application context, so should be no problem
object ContextHolder {
  
  private lateinit var _context: Context
  
  val context: Context
    get() = _context
  
  fun init(context: Context) {
    _context = context
  }
}