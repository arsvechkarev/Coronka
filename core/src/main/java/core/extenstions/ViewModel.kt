package core.extenstions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> Fragment.createViewModel(vararg args: Any): T {
  return ViewModelProvider(viewModelStore, factory(args)).get(T::class.java)
}

inline fun <reified T : ViewModel> AppCompatActivity.createViewModel(vararg args: Any): T {
  return ViewModelProvider(viewModelStore, factory(args)).get(T::class.java)
}

@Suppress("UNCHECKED_CAST")
fun factory(args: Array<out Any>) = object : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return modelClass.constructors[0].newInstance(*args) as T
  }
}