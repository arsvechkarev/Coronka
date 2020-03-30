package com.arsvechkarev.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import core.Loggable
import core.NetworkConnection
import core.log
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NetworkConnectionImpl(context: Context) : NetworkConnection, Loggable {
  
  override val logTag = "NetworkConnection"
  
  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  
  private val latch = CountDownLatch(1)
  
  private val callback = object : ConnectivityManager.NetworkCallback() {
    
    override fun onAvailable(network: Network) {
      log { "connection: available" }
      this@NetworkConnectionImpl.isConnected = true
      latch.countDown()
    }
    
    override fun onUnavailable() {
      log { "connection: unavailable" }
      this@NetworkConnectionImpl.isConnected = false
      latch.countDown()
    }
  }
  
  override var isConnected: Boolean = false
    private set
    get() {
      latch.await(1, TimeUnit.SECONDS)
      return field
    }
  
  init {
    connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
  }
}
