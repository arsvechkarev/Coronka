package core

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.ConnectivityManager.OnNetworkActiveListener

interface NetworkAvailabilityNotifier {
  
  fun registerListener(listener: OnNetworkActiveListener)
  
  fun unregisterListener(listener: OnNetworkActiveListener)
}

@SuppressLint("MissingPermission")
class AndroidNetworkAvailabilityNotifier(
  private val connectivityManager: ConnectivityManager
) : NetworkAvailabilityNotifier {
  
  override fun registerListener(listener: OnNetworkActiveListener) {
    connectivityManager.addDefaultNetworkActiveListener(listener)
  }
  
  override fun unregisterListener(listener: OnNetworkActiveListener) {
    connectivityManager.removeDefaultNetworkActiveListener(listener)
  }
}