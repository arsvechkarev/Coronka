package com.arsvechkarev.coronka.presentation

import base.HostActivity.DrawerOpenCloseListener
import core.DrawerState
import core.rx.RxSendingChannel

class DrawerLayoutStateListener(
  private val drawerStateSendingChannel: RxSendingChannel<DrawerState>
) : DrawerOpenCloseListener {
  
  override fun onDrawerOpened() {
    println("drawer_Opened")
    drawerStateSendingChannel.send(DrawerState(isOpened = true))
  }
  
  override fun onDrawerClosed() {
    println("drawer_Closed")
    drawerStateSendingChannel.send(DrawerState(isOpened = false))
  }
}