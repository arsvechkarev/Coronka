package com.arsvechkarev.coronka.presentation

import base.HostActivity.DrawerOpenCloseListener
import core.DrawerState
import core.rx.RxSendingChannel

class DrawerLayoutStateListener(
  private val drawerStateSendingChannel: RxSendingChannel<DrawerState>
) : DrawerOpenCloseListener {
  
  override fun onDrawerOpened() {
    drawerStateSendingChannel.send(DrawerState(isOpened = true))
  }
  
  override fun onDrawerClosed() {
    drawerStateSendingChannel.send(DrawerState(isOpened = false))
  }
}