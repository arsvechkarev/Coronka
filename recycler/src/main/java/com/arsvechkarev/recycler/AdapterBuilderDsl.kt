package com.arsvechkarev.recycler

import androidx.recyclerview.widget.RecyclerView
import api.recycler.DisplayableItem
import kotlin.reflect.KClass

fun adapter(block: AdapterBuilder.() -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder> {
  return AdapterBuilder().apply(block).build()
}

class AdapterBuilder {
  
  private val delegates = ArrayList<DslAdapterDelegate<out DisplayableItem>>()
  private val data = ArrayList<DisplayableItem>()
  
  fun <T : DisplayableItem> delegate(
    modelClass: KClass<T>,
    block: StaticDelegateBuilder<T>.() -> Unit
  ) {
    val builder = StaticDelegateBuilder<T>().apply(block)
    data.addAll(builder.data)
    
    @Suppress("UNCHECKED_CAST")
    val delegate = DslAdapterDelegate(modelClass, builder) as DslAdapterDelegate<DisplayableItem>
    delegates.add(delegate)
  }
  
  internal fun build(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
    val adapter = object : Adapter() {}
    adapter.addDelegates(delegates)
    adapter.submitList(data)
    return adapter
  }
}

class StaticDelegateBuilder<T> : DelegateBuilder<T>() {
  
  internal var data: List<T> = emptyList()
  
  fun data(data: List<T>) {
    this.data = data
  }
  
  fun data(item: T) {
    this.data = listOf(item)
  }
}