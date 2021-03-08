package core.recycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.arsvechkarev.viewdsl.ViewBuilder
import com.arsvechkarev.viewdsl.inflate
import core.DifferentiableItem
import core.DisplayableItem
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
  private var _simpleViewBuilder: ((parent: View) -> View)? = null
  private var _viewBuilder: (ViewBuilder.(View) -> View)? = null
  private var _viewHolderInitializer: (DslDelegateViewHolder<T>.() -> Unit) = { }
  private var _onBind: (View, T) -> Unit = { _, _ -> }
  private var _onRecycled: ((itemView: View) -> Unit)? = null
  
  fun layoutRes(@LayoutRes res: Int) {
    _layoutRes = res
  }
  
  fun view(builder: (parent: View) -> View) {
    _simpleViewBuilder = builder
  }
  
  fun buildView(builder: ViewBuilder.(View) -> View) {
    _viewBuilder = builder
  }
  
  fun onInitViewHolder(function: DslDelegateViewHolder<T>.() -> Unit) {
    _viewHolderInitializer = function
  }
  
  fun onBind(function: (itemView: View, element: T) -> Unit) {
    _onBind = function
  }
  
  fun onRecycled(function: (itemView: View) -> Unit) {
    _onRecycled = function
  }
  
  internal fun createViewHolder(parent: ViewGroup): DslDelegateViewHolder<T> {
    val view = when {
      _layoutRes != -1 -> {
        parent.inflate(_layoutRes)
      }
      _simpleViewBuilder != null -> {
        _simpleViewBuilder!!.invoke(parent)
      }
      _viewBuilder != null -> {
        val builder = ViewBuilder(parent.context)
        _viewBuilder!!.invoke(builder, parent)
      }
      else -> throw IllegalArgumentException("Cannot create view holder")
    }
    return DslDelegateViewHolder(view, _onBind, _onRecycled).apply(_viewHolderInitializer)
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
  private val onBindFunction: (View, T) -> Unit,
  private val _onRecycled: ((itemView: View) -> Unit)?
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
  
  override fun onViewRecycled() {
    _onRecycled?.invoke(itemView)
  }
}
