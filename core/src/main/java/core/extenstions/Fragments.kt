package core.extenstions

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

fun Fragment.addBackPressedCallback(callback: OnBackPressedCallback) {
  (activity as ComponentActivity).onBackPressedDispatcher.addCallback(callback)
}