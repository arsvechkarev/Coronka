package com.arsvechkarev.coronka.presentation

import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import base.HostActivity.DrawerOpenCloseListener
import base.views.DrawerLayout
import core.DrawerState
import core.di.CoreComponent.drawerStateSendingChannel

class DrawerLayoutStateObserver(
  private val drawerLayout: DrawerLayout,
) : DrawerOpenCloseListener, LifecycleObserver {
  
  override fun onDrawerOpened() {
    drawerStateSendingChannel.send(DrawerState(isOpened = true))
  }
  
  override fun onDrawerClosed() {
    drawerStateSendingChannel.send(DrawerState(isOpened = false))
  }
  
  @OnLifecycleEvent(ON_START)
  fun onStart() {
    drawerLayout.addOpenCloseListener(this)
  }
  
  @OnLifecycleEvent(ON_STOP)
  fun onStop() {
    drawerLayout.removeOpenCloseListener(this)
  }
}