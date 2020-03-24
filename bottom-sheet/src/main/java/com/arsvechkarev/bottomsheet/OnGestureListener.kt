package com.arsvechkarev.bottomsheet

import android.view.GestureDetector
import android.view.MotionEvent

interface OnGestureListener : GestureDetector.OnGestureListener {
  
  override fun onShowPress(e: MotionEvent) {}
  
  override fun onSingleTapUp(e: MotionEvent) = false
  
  override fun onDown(e: MotionEvent): Boolean = false
  
  override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float) = false
  
  override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) = false
  
  override fun onLongPress(e: MotionEvent?) {}
}