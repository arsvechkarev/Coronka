package core.recycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import core.extenstions.inflate
import kotlin.reflect.KClass

inline fun <reified T : DifferentiableItem> delegate(
  block: DelegateBuilder<T>.() -> Unit
): DslListAdapterDelegate<T> {
  val builder = DelegateBuilder<T>().apply(block)
  return DslListAdapterDelegate(T::class, builder)
}

inline fun <reified T : DisplayableItem> delegate(
  block: DelegateBuilder<T>.() -> Unit
): DslAdapterDelegate<T> {
  val builder = DelegateBuilder<T>().apply(block)
  return DslAdapterDelegate(T::class, builder)
}

open class DelegateBuilder<T> {
  
  private var _layoutRes: Int = -1
  private var _view: ((parent: View) -> View)? = null
  private var _viewHolderInitializer: (DslDelegateViewHolder<T>.() -> Unit) = { }
  private var _onBind: (View, T) -> Unit = { _, _ -> }
  
  fun layoutRes(@LayoutRes res: Int) {
    _layoutRes = res
  }
  
  fun view(builder: (parent: View) -> View) {
    _view = builder
  }
  
  fun onInitViewHolder(function: DslDelegateViewHolder<T>.() -> Unit) {
    _viewHolderInitializer = function
  }
  
  fun onBind(function: (itemView: View, element: T) -> Unit) {
    _onBind = function
  }
  
  internal fun createViewHolder(parent: ViewGroup): DslDelegateViewHolder<T> {
    val view = when {
      _layoutRes != -1 -> {
        parent.inflate(_layoutRes)
      }
      _view != null -> {
        _view!!.invoke(parent)
      }
      else -> throw IllegalArgumentException("Cannot create view holder")
    }
    return DslDelegateViewHolder(view, _onBind).apply(_viewHolderInitializer)
  }
}

class DslListAdapterDelegate<T : DifferentiableItem>(
  klass: KClass<T>,
  private val delegateBuilder: DelegateBuilder<T>
) : ListAdapterDelegate<T>(klass) {
  
  override fun onCreateViewHolder(parent: ViewGroup): DelegateViewHolder<T> {
    return delegateBuilder.createViewHolder(parent)
  }
  
  override fun onBindViewHolder(holder: DelegateViewHolder<T>, item: T) {
    (holder as DslDelegateViewHolder)._item = item
    holder.bind(item)
  }
}

class DslAdapterDelegate<T : DisplayableItem>(
  klass: KClass<T>,
  private val delegateBuilder: DelegateBuilder<T>
) : AdapterDelegate<T>(klass) {
  
  override fun onCreateViewHolder(parent: ViewGroup): DelegateViewHolder<T> {
    return delegateBuilder.createViewHolder(parent)
  }
  
  override fun onBindViewHolder(holder: DelegateViewHolder<T>, item: T) {
    (holder as DslDelegateViewHolder)._item = item
    holder.bind(item)
  }
}

class DslDelegateViewHolder<T>(
  itemView: View,
  private val onBindFunction: (View, T) -> Unit
) : DelegateViewHolder<T>(itemView) {
  
  internal var _item: Any? = null
  
  val item: T
    get() = if (_item == null) {
      throw IllegalArgumentException("Item has not been set yet")
    } else {
      @Suppress("UNCHECKED_CAST")
      _item as T
    }
  
  override fun bind(item: T) = onBindFunction(itemView, item)
}
