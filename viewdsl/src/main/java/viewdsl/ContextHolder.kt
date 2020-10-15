@file:Suppress("ObjectPropertyName")

package viewdsl

import android.content.Context

object ContextHolder {
  
  private lateinit var _context: Context
  
  val context: Context
    get() = _context
  
  fun init(context: Context) {
    this._context = context
  }
}