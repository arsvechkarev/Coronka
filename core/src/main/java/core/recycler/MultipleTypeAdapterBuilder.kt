package core.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import core.extenstions.inflate
import kotlin.reflect.KClass

class AdapterDelegateBuilder<T : DisplayableItem>(private val modelClass: KClass<T>) {
  
  internal var data: List<T>? = emptyList()
  
  private var layoutRes = -1
  private var onViewHolderInitialization: (AdapterViewHolder<T>, List<T>) -> Unit = { _, _ -> }
  private var onViewHolderBind: (View, T) -> Unit = { _, _ -> }
  
  fun layoutRes(layoutRes: Int) {
    this.layoutRes = layoutRes
  }
  
  fun data(data: List<T>) {
    this.data = data
  }
  
  fun data(item: T) {
    this.data = listOf(item)
  }
  
  fun onViewHolderInitialization(block: (AdapterViewHolder<T>, List<T>) -> Unit) {
    this.onViewHolderInitialization = block
  }
  
  fun onBindViewHolder(block: (View, T) -> Unit) {
    this.onViewHolderBind = block
  }
  
  internal fun build(): AdapterDelegate {
    return object : AdapterDelegate(modelClass) {
      
      override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return AdapterViewHolder(parent.inflate(layoutRes),
          onViewHolderInitialization, data!!)
      }
      
      @Suppress("UNCHECKED_CAST")
      override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
        val adapterViewHolder = holder as AdapterViewHolder<T>
        onViewHolderBind(adapterViewHolder.itemView, item as T)
      }
    }
  }
  
  class AdapterViewHolder<T>(
    itemView: View,
    onViewHolderInitialization: (AdapterViewHolder<T>, List<T>) -> Unit = { _, _ -> },
    data: List<T>
  ) : RecyclerView.ViewHolder(itemView) {
    
    init {
      onViewHolderInitialization(this, data)
    }
  }
}

class MultipleTypeAdapterBuilder {
  
  private val delegates = ArrayList<AdapterDelegate>()
  private val data = ArrayList<DisplayableItem>()
  
  fun <T : DisplayableItem> delegate(
    modelClass: KClass<T>,
    block: AdapterDelegateBuilder<T>.() -> Unit
  ) {
    val adapterDelegateBuilder = AdapterDelegateBuilder(modelClass).apply(block)
    data.addAll(adapterDelegateBuilder.data!!)
    delegates.add(adapterDelegateBuilder.build())
  }
  
  internal fun build(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
    val adapter = object : BaseAdapter(delegates) {}
    adapter.submitList(data)
    return adapter
  }
}

fun createAdapter(block: MultipleTypeAdapterBuilder.() -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder> {
  return MultipleTypeAdapterBuilder().apply(block).build()
}