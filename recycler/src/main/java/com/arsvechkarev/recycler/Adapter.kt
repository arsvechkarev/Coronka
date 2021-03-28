package com.arsvechkarev.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import api.recycler.DisplayableItem
import kotlin.reflect.KClass

abstract class Adapter : RecyclerView.Adapter<ViewHolder>() {
  
  protected var data: List<DisplayableItem> = ArrayList()
  
  private val classesToViewTypes = HashMap<KClass<*>, Int>()
  private val delegatesSparseArray = SparseArrayCompat<AdapterDelegate<out DisplayableItem>>()
  private val delegates = ArrayList<AdapterDelegate<out DisplayableItem>>()
  
  fun addDelegates(vararg delegates: AdapterDelegate<out DisplayableItem>) {
    addDelegates(delegates.toList())
  }
  
  fun addDelegates(delegates: List<AdapterDelegate<out DisplayableItem>>) {
    this.delegates.addAll(delegates)
    delegates.forEachIndexed { i, delegate ->
      classesToViewTypes[delegate.modelClass] = i
      delegatesSparseArray.put(i, delegate)
    }
  }
  
  fun submitList(list: List<DisplayableItem>?) {
    data = list ?: ArrayList()
    notifyDataSetChanged()
  }
  
  fun changeListWithCrossFadeAnimation(list: List<DisplayableItem>) {
    if (list === data) return
    val alwaysFalseCallback = AlwaysFalseCallback(data, list)
    data = list
    val diffResult = DiffUtil.calculateDiff(alwaysFalseCallback)
    diffResult.dispatchUpdatesTo(this)
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    delegates.forEach { it.onAttachedToRecyclerView(recyclerView) }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val delegate = delegatesSparseArray[viewType] ?: error("No delegate for view type $viewType")
    return delegate.onCreateViewHolder(parent)
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val adapterDelegate = delegatesSparseArray[getItemViewType(position)]
        ?: error("No delegate for position $position")
    adapterDelegate.onBindViewHolderRaw(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return classesToViewTypes[data[position]::class] ?: error(
      "Can't find delegate for position: $position")
  }
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}