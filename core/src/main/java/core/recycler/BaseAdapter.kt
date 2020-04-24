package core.recycler

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import core.extenstions.forEach

abstract class BaseAdapter : RecyclerView.Adapter<ViewHolder>() {
  
  protected var data: List<DisplayableItem> = ArrayList()
  private val delegates = SparseArrayCompat<AdapterDelegate>()
  
  protected fun addDelegate(delegate: AdapterDelegate) {
    delegates.put(delegate.modelClass.hashCode(), delegate)
  }
  
  fun submitList(list: List<DisplayableItem>?) {
    data = list ?: ArrayList()
    notifyDataSetChanged()
  }
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    delegates.forEach { it.onAttachedToRecyclerView(recyclerView) }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return delegates[viewType]?.onCreateViewHolder(parent)
        ?: error("Can't find delegate for type $viewType")
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val adapterDelegate = delegates[getItemViewType(position)]
        ?: error("Can't find delegate for position: $position")
    adapterDelegate.onBindViewHolder(holder, data[position])
  }
  
  override fun getItemViewType(position: Int): Int {
    return data[position]::class.hashCode()
  }
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    delegates.forEach { it.onDetachedFromRecyclerView(recyclerView) }
  }
}